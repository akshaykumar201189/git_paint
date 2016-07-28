package com.pied.piper.core.services.impl;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.pied.piper.core.db.dao.impl.UserDaoImpl;
import com.pied.piper.core.db.model.User;
import com.pied.piper.core.db.model.UserRelations;
import com.pied.piper.core.dto.ImageMetaData;
import com.pied.piper.core.dto.SearchUserRequestDto;
import com.pied.piper.core.dto.UserDetails;
import com.pied.piper.core.dto.user.SignInRequestDto;
import com.pied.piper.core.services.interfaces.GalleriaService;
import com.pied.piper.core.services.interfaces.UserService;
import com.pied.piper.util.FacebookUtils;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by palash.v on 21/07/16.
 */
public class UserServiceImpl implements UserService {

    private final UserDaoImpl userDao;
    private final GalleriaService galleriaService;

    @Inject
    public UserServiceImpl(UserDaoImpl userDao, GalleriaService galleriaService) {
        this.userDao = userDao;
        this.galleriaService = galleriaService;
    }

    @Override
    @Transactional
    public List<User> searchUser(SearchUserRequestDto searchUserRequestDto) {
        return userDao.searchUser(searchUserRequestDto);
    }

    @Override
    @Transactional
    public User findByAccountId(String accountId) {
        if(StringUtils.isEmpty(accountId))
            return null;
        SearchUserRequestDto searchDto = new SearchUserRequestDto();
        searchDto.setAccountId(accountId);
        List<User> users = searchUser(searchDto);
        if(users == null || users.size() == 0)
            return null;
        return users.get(0);
    }

    @Override
    @Transactional
    public UserDetails getUserDetailsByUserId(Long userId) {
        User user = userDao.fetchById(userId);
        if(user == null)
            return null;

        UserDetails userDetails = new UserDetails(user);
        List<ImageMetaData> metaDataList = galleriaService.getImageMetaData(user.getAccountId());

        if(!metaDataList.isEmpty()){
            List<Long> imageIds = new ArrayList<>();
            for(ImageMetaData metaData : metaDataList)
                imageIds.add(metaData.getImageId());
            userDetails.setOwnImageIds(imageIds);
        }
        userDetails.setFollowers(getFollowers(user.getAccountId()));

        return userDetails;
    }

    @Override
    @Transactional
    public User findById(Long userId) {
        if(userId == null) return null;
        return userDao.fetchById(userId);
    }

    @Override
    @Transactional
    public List<User> getFollowers(String accountId) {
        User user = findByAccountId(accountId);
        List<User> followers = new ArrayList<>();
        if(user!=null && user.getFollowers()!=null && user.getFollowers().size()>0) {
            followers = user.getFollowers().stream().map(userRelations -> findById(userRelations.getDestinationUserId())).collect(Collectors.toList());
        }
        return followers;
    }

    @Override
    @Transactional
    public List<User> createFollower(String userId, String followerId) {
        addFollower(userId, followerId);
        return getFollowers(userId);
    }

    private void addFollower(String userId, String followerId) {
        User user = findByAccountId(userId);
        if (user.getFollowers() == null) {
            user.setFollowers(new ArrayList<>());
        }
        User follower = findByAccountId(followerId);
        if(follower!=null) {
            UserRelations userRelation = new UserRelations();
            userRelation.setSourceUser(user);
            userRelation.setDestinationUserId(follower.getUserId());
            user.getFollowers().add(userRelation);
        }
    }

    @Override
    public List<List<ImageMetaData>> getFollowerImages(String userId) {
        List<User> followers = getFollowers(userId);
        List<List<ImageMetaData>> followerImages = new ArrayList<>();
        followers.stream().forEach(follower -> followerImages.add(galleriaService.getImageMetaData(follower.getAccountId())));
        return followerImages;
    }

    @Override
    @Transactional
    public void signInUser(SignInRequestDto signInRequestDto) {

        // Search existing user
        SearchUserRequestDto searchUserRequestDto = new SearchUserRequestDto();
        searchUserRequestDto.setAccountId(signInRequestDto.getUserDetails().getUserId());
        List<User> users = userDao.searchUser(searchUserRequestDto);
        User user = null;
        if(users.isEmpty()) {
            // If not present, create an entry in User table
            user = new User();
            UserDetails userDetails = signInRequestDto.getUserDetails();
            user.setAccountId(userDetails.getUserId());
            user.setAvatarUrl(userDetails.getAvatarUrl());
            user.setName(userDetails.getName());
            user.setEmailId(userDetails.getEmail());
            userDao.save(user);
        } else {
            user = users.get(0);
        }

        // Get Friends from facebook and follow them if not already followed
        String accessToken = signInRequestDto.getOAuthCredentials().getOAuthResponse().getAccessToken();
        FacebookClient client = new DefaultFacebookClient(accessToken, Version.VERSION_2_5);
        List<com.restfb.types.User> friends = FacebookUtils.findFriends(client, user.getAccountId());

        List<UserRelations> userRelationsList = user.getFollowers();
        List<String> followersAccountIds = new ArrayList<>();
        for(UserRelations userRelations : userRelationsList) {
            followersAccountIds.add(userDao.fetchById(userRelations.getDestinationUserId()).getAccountId());
        }

        for(com.restfb.types.User friend : friends) {
            if(!followersAccountIds.contains(friend.getId())) {
                addFollower(user.getAccountId(),friend.getId());
            }
        }

    }

}

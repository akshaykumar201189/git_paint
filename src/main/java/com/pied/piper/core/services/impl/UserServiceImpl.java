package com.pied.piper.core.services.impl;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.pied.piper.core.db.dao.impl.UserDaoImpl;
import com.pied.piper.core.db.dao.impl.UserRelationsDaoImpl;
import com.pied.piper.core.db.model.User;
import com.pied.piper.core.db.model.UserRelations;
import com.pied.piper.core.dto.ImageMetaData;
import com.pied.piper.core.dto.ProfileDetails;
import com.pied.piper.core.dto.SearchUserRequestDto;
import com.pied.piper.core.dto.UserDetails;
import com.pied.piper.core.dto.user.SignInRequestDto;
import com.pied.piper.core.dto.user.SignInResponseDto;
import com.pied.piper.core.services.interfaces.GalleriaService;
import com.pied.piper.core.services.interfaces.SessionService;
import com.pied.piper.core.services.interfaces.UserService;
import com.pied.piper.exception.ResponseException;
import com.pied.piper.util.FacebookUtils;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by palash.v on 21/07/16.
 */
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDaoImpl userDao;
    private final GalleriaService galleriaService;
    private final SessionService sessionService;
    private final UserRelationsDaoImpl userRelationsDao;

    @Inject
    public UserServiceImpl(UserDaoImpl userDao, GalleriaService galleriaService, SessionService sessionService, UserRelationsDaoImpl userRelationsDao) {
        this.userDao = userDao;
        this.galleriaService = galleriaService;
        this.sessionService = sessionService;
        this.userRelationsDao = userRelationsDao;
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
    public void addFollower(String userId, String followerId) {
        User user = findByAccountId(userId);
        if (user.getFollowers() == null) {
            user.setFollowers(new ArrayList<>());
        }
        User follower = findByAccountId(followerId);
        if(follower!=null) {
            UserRelations userRelation = new UserRelations();
            userRelation.setSourceUser(user);
            userRelation.setDestinationUserId(follower.getId());
            user.getFollowers().add(userRelation);
            userRelationsDao.save(userRelation);
        }
    }

    @Override
    public List<List<ImageMetaData>> getFollowerImages(String userId) {
        List<User> followers = getFollowers(userId);
        List<List<ImageMetaData>> followerImages = new ArrayList<>();
        followers.stream().forEach(follower -> followerImages.add(galleriaService.getImageMetaData(follower.getAccountId())));
        return followerImages;
    }

    /**
     * API for signing In
     * TODO: Implement Security and Validation of Inputs
     * @param signInRequestDto
     */
    @Override
    @Transactional
    public SignInResponseDto signInUser(SignInRequestDto signInRequestDto) {

        // Search existing user
        SearchUserRequestDto searchUserRequestDto = new SearchUserRequestDto();
        searchUserRequestDto.setAccountId(signInRequestDto.getUserDetails().getId());
        List<User> users = userDao.searchUser(searchUserRequestDto);
        User user = null;
        if(users.isEmpty()) {
            // If not present, create an entry in User table
            user = new User();
            UserDetails userDetails = signInRequestDto.getUserDetails();
            user.setAccountId(userDetails.getId());
            user.setAvatarUrl(userDetails.getAvatarUrl());
            user.setName(userDetails.getName());
            user.setEmailId(userDetails.getEmail());
            userDao.save(user);

            // Get Friends from facebook and follow them if not already followed
            List<com.restfb.types.User> friends = null;
            FacebookClient client = null;
            try {
                String accessToken = signInRequestDto.getOAuthCredentials().getOAuthResponse().getAccessToken();
                client = new DefaultFacebookClient(accessToken, Version.VERSION_2_5);
                friends = FacebookUtils.findFriends(client, user.getAccountId());
                com.restfb.types.User fbUser = FacebookUtils.getUserDetails(client);
                Validate.isTrue(fbUser.getId().equals(signInRequestDto.getUserDetails().getId()));
            } catch (Exception e) {
                throw new ResponseException("Authentication error while accessing facebook");
            }

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

            // TODO: Query Facebook and Store full profile image
            String fullProfileUrl = null;
            fullProfileUrl = FacebookUtils.getFullProfileImageUrl(client);
            user.setFullProfileUrl(fullProfileUrl);

        } else {
            user = users.get(0);
        }

        // Create a new Session for this user
        String sessionId = sessionService.createSessionForAccount(user.getAccountId());
        ProfileDetails profileDetails = null;

        // Get Profile Details
        try {
            profileDetails = galleriaService.getProfileDetails(user.getAccountId(), user.getAccountId());
        } catch (Exception e) {
            log.error("Error in getting profile details of user " + user.getAccountId());
        }

        SignInResponseDto responseDto = new SignInResponseDto();
        responseDto.setSessionId(sessionId);
        responseDto.setProfileDetails(profileDetails);

        return responseDto;
    }

}

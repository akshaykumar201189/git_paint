package com.pied.piper.core.services.impl;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.pied.piper.core.db.dao.impl.ImageDaoImpl;
import com.pied.piper.core.db.model.Image;
import com.pied.piper.core.db.model.ImageRelation;
import com.pied.piper.core.db.model.ImageTags;
import com.pied.piper.core.db.model.User;
import com.pied.piper.core.db.model.enums.ApprovalStatusEnum;
import com.pied.piper.core.dto.*;
import com.pied.piper.core.services.interfaces.GalleriaService;
import com.pied.piper.core.services.interfaces.ImageRelationService;
import com.pied.piper.core.services.interfaces.UserService;
import com.pied.piper.exception.ResponseException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by akshay.kesarwan on 21/05/16.
 */
@Slf4j
public class GalleriaServiceImpl implements GalleriaService {

    private final ImageDaoImpl imageDao;
    private final ImageTagServiceImpl imageTagService;
    private final UserService userService;
    private final ImageRelationService imageRelationService;

    @Inject
    public GalleriaServiceImpl(ImageDaoImpl imageDao, ImageTagServiceImpl imageTagService, UserService userService, ImageRelationService imageRelationService) {
        this.imageDao = imageDao;
        this.imageTagService = imageTagService;
        this.userService = userService;
        this.imageRelationService = imageRelationService;
    }

    @Override
    @Transactional
    public Long saveImage(SaveImageRequestDto saveImageRequestDto) throws Exception {
        try {
            Image image = null;
            if(saveImageRequestDto.getImageId()!=null) {
                image = imageDao.fetchById(saveImageRequestDto.getImageId());
            }
            if (image == null) {
                image = new Image();
            }

            if (saveImageRequestDto.getImage() != null) {
                String imageStr = saveImageRequestDto.getImage();
                imageStr = imageStr.substring(imageStr.indexOf(",")+1);
                image.setImage(imageStr);
            }

            if (saveImageRequestDto.getDescription() != null)
                image.setDescription(saveImageRequestDto.getDescription());

            if (saveImageRequestDto.getTitle() != null)
                image.setTitle(saveImageRequestDto.getTitle());

            if (saveImageRequestDto.getAccountId() != null)
                image.setAccountId(saveImageRequestDto.getAccountId());

            imageDao.save(image);

            // Add tags
            if (!saveImageRequestDto.getTags().isEmpty()) {
                List<ImageTags> imageTagses = new ArrayList<>();
                for(String tag : saveImageRequestDto.getTags()) {
                    ImageTags imageTags = new ImageTags();
                    imageTags.setSourceImage(image);
                    imageTags.setTag(tag);
                    imageTagService.saveImageTag(imageTags);
                    imageTagses.add(imageTags);
                }
                image.setTags(imageTagses);
            }

            imageDao.save(image);

            return image.getImageId();
        } catch (Exception e) {
            log.error("Error in saving Image " + e);
            throw e;
        }
    }

    @Override
    @Transactional
    public String getImageData(Long imageId) {
        Image image = getImage(imageId);
        if(image == null)
            return null;
        return image.getImage();
    }

    @Override
    @Transactional
    public ImageMetaData getImageMetaData(Long imageId) {
        List<Image> images = imageDao.getMetaData(imageId, null);
        if(images.size() == 0)
            return null;
        return new ImageMetaData(images.get(0));
    }

    @Override
    @Transactional
    public List<ImageMetaData> getImageMetaData(String accountId) {
        User user = userService.findByAccountId(accountId);
        List<Image> imageList = imageDao.getMetaData(null, accountId);
        List<ImageMetaData> metaDataList = new ArrayList<>();
        for(Image image : imageList){
            metaDataList.add(new ImageMetaData(image, user));
        }
        return metaDataList;
    }

    @Override
    @Transactional
    public List<Image> getImagesForAccountId(String accountId) {
        Criterion criterion = Restrictions.eq("accountId", accountId);
        List<Image> images = imageDao.findByCriteria(criterion);
        return images;
    }

    @Override
    @Transactional
    public Image getImage(Long imageId) {
        try {
            return imageDao.fetchById(imageId);
        } catch (Exception e) {
            log.error("Some error while fetching for " + imageId, e);
            throw new ResponseException("Error while fetching for " + imageId + ". " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public List<Image> findByAccountId(String accountId) {
        return imageDao.findByAccountId(accountId);
    }

    @Override
    @Transactional
    public SearchResponseDto search(String searchText) {
        // Search Tags
        List<ImageTags> imageTags = imageTagService.searchImageTags(searchText);
        List<String> tagStrings = imageTags.stream().map(imageTag -> imageTag.getTag()).collect(Collectors.toList());
        tagStrings = new ArrayList<>(new HashSet<>(tagStrings));

        // Search Users
        SearchUserRequestDto searchUserRequestDto = new SearchUserRequestDto();
        searchUserRequestDto.setUserLike(searchText);
        List<User> users = userService.searchUser(searchUserRequestDto);

        SearchResponseDto searchResponseDto = new SearchResponseDto();
        searchResponseDto.setTags(tagStrings);
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        if(users!=null && users.size()>0) {
            for(User user : users) {
                UserResponseDto userResponseDto = new UserResponseDto(user, null);
                userResponseDtos.add(userResponseDto);
            }
            searchResponseDto.setUsers(userResponseDtos);
        }
        return searchResponseDto;
    }

    @Override
    @Transactional
    public ProfileDetails getProfileDetails(String accountId, String ownerAccountId) throws Exception {

        ProfileDetails profileDetails = new ProfileDetails();

        // Check if follow
        if(!accountId.equals(ownerAccountId)) {
            try {
                List<User> followersOfOwner = userService.getFollowers(ownerAccountId);
                log.info("Followers " + followersOfOwner);
                if(followersOfOwner!=null && followersOfOwner.size()>0) {
                    List<String> accountIdsOfFFollowers = followersOfOwner.stream().map(user -> user.getAccountId()).collect(Collectors.toList());
                    if (accountIdsOfFFollowers.contains(accountId))
                        profileDetails.setIsFollower(true);
                    else
                        profileDetails.setIsFollower(false);
                }
            } catch (Exception e) {
                log.error("Exception in getting followers " + e);
                profileDetails.setIsFollower(false);
            }
        }

        // get user details
        SearchUserRequestDto searchUserRequestDto = new SearchUserRequestDto();
        searchUserRequestDto.setAccountId(accountId);
        List<User> users = userService.searchUser(searchUserRequestDto);
        if(users==null || users.size()==0)
            throw new ResponseException("User not found",500);
        List<User> followers = userService.getFollowers(users.get(0).getAccountId());
        UserResponseDto userResponseDto = new UserResponseDto(users.get(0), followers);
        profileDetails.setUser(userResponseDto);

        // get all images of user
        List<ImageMetaData> images = getImagesForAccountId(accountId).stream().map(image -> new ImageMetaData(image)).collect(Collectors.toList());

        // filter owned images
        List<ImageMetaData> ownedImages = images.stream().filter(image -> image.getIsCloned().equals(false)).collect(Collectors.toList());
        profileDetails.setOwnedImages(ownedImages);

        // filter cloned images
        List<ImageMetaData> clonedImages = images.stream().filter(image -> image.getIsCloned().equals(true)).collect(Collectors.toList());
        profileDetails.setClonedImages(clonedImages);

        // get Pull Request
        if(ownedImages!=null && ownedImages.size()>0) {
            List<ImageRelation> imageRelations = imageRelationService.getImageRelationsForSourceImageIds(ownedImages.stream().map(image -> image.getImageId()).collect(Collectors.toList()));
            imageRelations.removeIf(imageRelation -> !imageRelation.getApprovalStatus().equals(ApprovalStatusEnum.PENDING));
            List<PullRequest> pullRequests = new ArrayList<>();
            if(imageRelations!=null && imageRelations.size()>0) {
                List<Long> clonedImagesByOthersId = imageRelations.stream().map(imageRelation -> imageRelation.getClonedImage()).collect(Collectors.toList());
                Criterion idCriterion = Restrictions.in("imageId", clonedImagesByOthersId);
                List<Image> clonedImagesByOthers = imageDao.findByCriteria(idCriterion);
                List<String> accountIds = clonedImagesByOthers.stream().map(image -> image.getAccountId()).collect(Collectors.toList());
                SearchUserRequestDto userRequestDto = new SearchUserRequestDto();
                userRequestDto.setAccountIds(accountIds);
                List<User> usersList = userService.searchUser(userRequestDto);
                int index = 0;
                for(Image clonedImage : clonedImagesByOthers) {
                    PullRequest pullRequest = new PullRequest();
                    pullRequest.setPullRequestId(imageRelations.get(index).getId());
                    pullRequest.setImage(new ImageMetaData(clonedImage));
                    Long sourceImageId = imageRelations.get(index).getSourceImage();
                    pullRequest.setOriginalImage(new ImageMetaData(imageDao.fetchById(sourceImageId)));
                    pullRequest.setSender(new UserResponseDto(usersList.get(index), null));
                    pullRequests.add(pullRequest);
                    index++;
                }
            }
            profileDetails.setPullRequests(pullRequests);
        }
        return profileDetails;
    }

    @Override
    @Transactional
    public Long cloneImage(Long imageId, String accountId) throws Exception {
        try {
            Image image = imageDao.fetchById(imageId);
            Image clonedImage = new Image();
            clonedImage.setAccountId(accountId);
            clonedImage.setTitle(image.getTitle());
            clonedImage.setDescription(image.getDescription());
            clonedImage.setImage(image.getImage());
            clonedImage.setIsCloned(true);
            imageDao.save(clonedImage);
            List<ImageTags> newImageTags = new ArrayList<>();
            for (ImageTags imageTags : image.getTags()) {
                ImageTags newImageTag = new ImageTags();
                newImageTag.setTag(imageTags.getTag());
                newImageTag.setSourceImage(clonedImage);
                imageTagService.saveImageTag(newImageTag);
                newImageTags.add(newImageTag);
            }
            clonedImage.setTags(newImageTags);
            imageDao.save(clonedImage);

            ImageRelation imageRelation = new ImageRelation();
            imageRelation.setApprovalStatus(ApprovalStatusEnum.NEW);
            imageRelation.setClonedImage(clonedImage.getImageId());
            imageRelation.setSourceImage(imageId);

            imageRelationService.saveImageRelation(imageRelation);
            return clonedImage.getImageId();
        } catch (Exception e) {
            log.error("Error in cloning " , e);
            throw new ResponseException("Error in cloning", 500);
        }
    }

    @Override
    @Transactional
    public void sendPullRequest(Long imageId, String accountId) throws Exception {
        ImageRelation imageRelation = imageRelationService.getImageRelationsForClonedImageIds(Arrays.asList(imageId)).get(0);
        imageRelation.setApprovalStatus(ApprovalStatusEnum.PENDING);
    }

    @Override
    @Transactional
    public void approvePullRequest(Long prId, SaveImageRequestDto saveImageRequestDto) throws Exception {
        ImageRelation imageRelation = imageRelationService.findById(prId);
        imageRelation.setApprovalStatus(ApprovalStatusEnum.APPROVED);

        Image sourceImage = imageDao.fetchById(imageRelation.getSourceImage());
        sourceImage.setImage(saveImageRequestDto.getImage());
    }

    @Override
    @Transactional
    public void rejectPullRequest(Long prId) throws Exception {
        ImageRelation imageRelation = imageRelationService.findById(prId);
        imageRelation.setApprovalStatus(ApprovalStatusEnum.REJECTED);
    }

    @Override
    @Transactional
    public PullRequest getPullRequest(Long prId) {
        ImageRelation imageRelation = imageRelationService.findById(prId);
        if(imageRelation == null){
            throw new ResponseException("pr not found", Response.Status.NOT_FOUND);
        }

        ImageMetaData clonedImage = getImageMetaData(imageRelation.getClonedImage());
        if(clonedImage == null){
            throw new ResponseException("cloned image not found", Response.Status.BAD_REQUEST);
        }

        ImageMetaData originalImage = getImageMetaData(imageRelation.getSourceImage());
        if(originalImage == null){
            throw new ResponseException("original image not found", Response.Status.BAD_REQUEST);
        }

        PullRequest pullRequest = new PullRequest();
        pullRequest.setPullRequestId(imageRelation.getId());
        pullRequest.setImage(clonedImage);
        pullRequest.setOriginalImage(originalImage);

        User user = userService.findByAccountId(clonedImage.getAccountId());
        pullRequest.setSender(new UserResponseDto(user, null));

        return pullRequest;
    }
}

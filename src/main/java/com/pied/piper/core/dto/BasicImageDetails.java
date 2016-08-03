package com.pied.piper.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pied.piper.core.db.model.Image;
import com.pied.piper.core.db.model.ImageRelation;
import com.pied.piper.core.db.model.ImageTags;
import com.pied.piper.core.db.model.User;
import com.pied.piper.core.db.model.enums.ApprovalStatusEnum;
import com.pied.piper.core.enums.EntityStatus;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by akshay.kesarwan on 02/08/16.
 */
@Data
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicImageDetails {
    private Long id;
    private String title;
    private String description;
    private String url;
    private String accountId;
    private Integer numOfLikes;
    private Integer numOfComments;
    private EntityStatus entityStatus;
    private List<String> tags = new ArrayList<>();
    private Boolean isCloned;
    private Long sourceImageId;
    private String sourceImageUrl;
    private ApprovalStatusEnum approvalStatus;
    private List<User> topLikedUsers = new ArrayList<>();
    private Boolean isLikedbyMe;

    public BasicImageDetails(Image image) {
        this.id = image.getId();
        this.title = image.getTitle();
        this.description = image.getDescription();
        this.url = image.getImage();
        this.accountId = image.getAccountId();
        this.numOfLikes = image.getNumOfLikes();
        this.numOfComments = image.getNumOfComments();
        for(ImageTags imageTags : image.getTags())
            tags.add(imageTags.getTag());
        this.isCloned = image.getIsCloned();
        this.entityStatus = image.getEntityStatus();
    }

    public BasicImageDetails(Image image, List<User> topLikedUsers) {
        this.id = image.getId();
        this.title = image.getTitle();
        this.description = image.getDescription();
        this.url = image.getImage();
        this.accountId = image.getAccountId();
        this.numOfLikes = image.getNumOfLikes();
        this.numOfComments = image.getNumOfComments();
        for(ImageTags imageTags : image.getTags())
            tags.add(imageTags.getTag());
        this.isCloned = image.getIsCloned();
        this.entityStatus = image.getEntityStatus();
        if(topLikedUsers!=null && topLikedUsers.size() >0) {
            List<String> accountIdsOfLikedUsers = new ArrayList<>();
            Map<String , User> accountToUserMap = new HashMap<>();
            for(User user : topLikedUsers) {
                accountIdsOfLikedUsers.add(user.getAccountId());
                accountToUserMap.put(user.getAccountId(), user);
            }
            if (accountIdsOfLikedUsers != null && accountIdsOfLikedUsers.size() > 0) {
                if (accountIdsOfLikedUsers.contains(image.getAccountId())) {
                    isLikedbyMe = true;
                    accountIdsOfLikedUsers.remove(image.getAccountId());
                } else
                    isLikedbyMe = false;
                if (accountIdsOfLikedUsers.size() > 0) {
                    int counter = 0;
                    while(counter < 4 && accountIdsOfLikedUsers!=null) {
                        topLikedUsers.add(accountToUserMap.get(accountIdsOfLikedUsers.get(counter++)));
                    }
                }
            }
        }
    }

    public BasicImageDetails(Image image, ImageRelation imageRelation, List<User> topLikedUsers) {
        this.id = image.getId();
        this.title = image.getTitle();
        this.description = image.getDescription();
        this.url = image.getImage();
        this.accountId = image.getAccountId();
        this.numOfLikes = image.getNumOfLikes();
        this.numOfComments = image.getNumOfComments();
        for(ImageTags imageTags : image.getTags())
            tags.add(imageTags.getTag());
        this.isCloned = image.getIsCloned();
        this.topLikedUsers = topLikedUsers;
        this.sourceImageId = imageRelation.getSourceImage();
        this.approvalStatus = imageRelation.getApprovalStatus();
        this.entityStatus = image.getEntityStatus();
        if(topLikedUsers!=null && topLikedUsers.size() >0) {
            List<String> accountIdsOfLikedUsers = new ArrayList<>();
            Map<String , User> accountToUserMap = new HashMap<>();
            for(User user : topLikedUsers) {
                accountIdsOfLikedUsers.add(user.getAccountId());
                accountToUserMap.put(user.getAccountId(), user);
            }
            if (accountIdsOfLikedUsers != null && accountIdsOfLikedUsers.size() > 0) {
                if (accountIdsOfLikedUsers.contains(image.getAccountId())) {
                    isLikedbyMe = true;
                    accountIdsOfLikedUsers.remove(image.getAccountId());
                } else
                    isLikedbyMe = false;
                if (accountIdsOfLikedUsers.size() > 0) {
                    int counter = 0;
                    while(counter < 4 && accountIdsOfLikedUsers!=null) {
                        topLikedUsers.add(accountToUserMap.get(accountIdsOfLikedUsers.get(counter++)));
                    }
                }
            }
        }
    }
}

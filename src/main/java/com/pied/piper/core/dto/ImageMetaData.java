package com.pied.piper.core.dto;

import com.pied.piper.core.db.model.Image;
import com.pied.piper.core.db.model.ImageTags;
import com.pied.piper.core.db.model.User;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

import java.util.List;

/**
 * Created by ankit.c on 21/07/16.
 */
@Data
@JsonSnakeCase
public class ImageMetaData {
    private Long imageId;
    private String title;
    private String description;
    private String accountId;
    private String userAvatarUrl;
    private String userName;
    private Integer numOfLikes;
    private Boolean isCloned;
    private List<ImageTags> tags;
    private Long sourceImageId;

    public ImageMetaData(){}

    public ImageMetaData(Image image){
        this.imageId = image.getImageId();
        this.title = image.getTitle();
        this.description = image.getDescription();
        this.accountId = image.getAccountId();
        this.numOfLikes = image.getNumOfLikes();
        this.isCloned = image.getIsCloned();
        this.tags = image.getTags();
    }

    public ImageMetaData(Image image, User user){
        this.imageId = image.getImageId();
        this.title = image.getTitle();
        this.description = image.getDescription();
        this.accountId = image.getAccountId();
        this.numOfLikes = image.getNumOfLikes();
        this.isCloned = image.getIsCloned();
        this.tags = image.getTags();
        this.userAvatarUrl = user.getAvatarUrl();
        this.userName = user.getName();
    }
}

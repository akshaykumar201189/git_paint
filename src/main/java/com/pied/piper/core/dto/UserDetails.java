package com.pied.piper.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pied.piper.core.db.model.User;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankit.c on 21/07/16.
 */
@Data
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetails {
    private String avatarUrl;
    private String name;
    private String email;
    private String userId;
    private List<Long> ownImageIds = new ArrayList<>();
    private List<Long> clonedImageIds = new ArrayList<>();
    private List<Long> prIds = new ArrayList<>();
    private List<User> followers;

    public UserDetails(){}

    public UserDetails(User user){
        this.name = user.getName();
        this.avatarUrl = user.getAvatarUrl();
    }
}

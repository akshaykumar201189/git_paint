package com.pied.piper.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pied.piper.core.db.model.User;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

import java.util.List;

/**
 * Created by akshay.kesarwan on 21/07/16.
 */
@Data
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponseDto {
    private Long userId;

    private String accountId;

    private String name;

    private String avatarUrl;

    private List<User> followers;

    private String fullProfileUrl;

    public UserResponseDto(User user, List<User> followers) {
        this.userId = user.getId();
        this.accountId = user.getAccountId();
        this.name = user.getName();
        this.avatarUrl = user.getAvatarUrl();
        this.followers = followers;
        this.fullProfileUrl = user.getFullProfileUrl();
    }
}

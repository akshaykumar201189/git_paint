package com.pied.piper.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshay.kesarwan on 22/07/16.
 */
@Data
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDetails {
    private Boolean isFollower;
    private UserResponseDto user;
    private List<BasicImageDetails> ownedImages = new ArrayList<>();
    private List<BasicImageDetails> clonedImages = new ArrayList<>();
    private List<PullRequest> pullRequests = new ArrayList<>();
}

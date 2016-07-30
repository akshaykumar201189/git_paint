package com.pied.piper.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pied.piper.core.db.model.Image;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

/**
 * Created by akshay.kesarwan on 22/07/16.
 */
@Data
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest {
    private Long pullRequestId;
    private UserResponseDto sender;
    private Image image;
    private Image originalImage;
}

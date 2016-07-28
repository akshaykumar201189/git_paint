package com.pied.piper.core.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

/**
 * Created by akshay.kesarwan on 28/07/16.
 */
@Data
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthResponse {
    private String accessToken;
}

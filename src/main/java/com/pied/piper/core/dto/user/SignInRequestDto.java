package com.pied.piper.core.dto.user;

import com.pied.piper.core.dto.UserDetails;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

import javax.validation.Valid;

/**
 * Created by akshay.kesarwan on 28/07/16.
 */
@Data
@JsonSnakeCase
public class SignInRequestDto {
    @Valid
    private OAuthCredentials oAuthCredentials;
    @Valid
    private UserDetails userDetails;
}

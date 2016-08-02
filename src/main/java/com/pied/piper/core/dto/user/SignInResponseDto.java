package com.pied.piper.core.dto.user;

import com.pied.piper.core.dto.ProfileDetails;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

/**
 * Created by akshay.kesarwan on 01/08/16.
 */
@Data
@JsonSnakeCase
public class SignInResponseDto {
    private String sessionId;
    private ProfileDetails profileDetails;
}

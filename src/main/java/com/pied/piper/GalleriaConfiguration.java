package com.pied.piper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pied.piper.core.config.AwsCredentials;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by akshay.kesarwan on 21/05/16.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GalleriaConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonProperty
    private AwsCredentials awsCredentials;

    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;
}

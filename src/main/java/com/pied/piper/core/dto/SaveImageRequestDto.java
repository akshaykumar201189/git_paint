package com.pied.piper.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by akshay.kesarwan on 21/07/16.
 */
@JsonSnakeCase
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveImageRequestDto {

    @JsonProperty(value = "image_id")
    private Long imageId;

    @NotNull
    private String image;

    private List<String> tags;

    private String description;

    private String title;

    @NotNull
    private String accountId;
}

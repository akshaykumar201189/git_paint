package com.pied.piper.core.db.model;

import com.fasterxml.jackson.annotation.*;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by akshay.kesarwan on 21/07/16.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSnakeCase
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class ImageTags extends BaseEntity {

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "source_image_id")
    @JsonProperty(value = "source_image_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Image sourceImage;

    @Column(name = "tag")
    private String tag;

}

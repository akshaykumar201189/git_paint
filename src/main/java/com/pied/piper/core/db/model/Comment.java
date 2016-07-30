package com.pied.piper.core.db.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pied.piper.serialiser.ImageSerialiser;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by ankit.c on 22/07/16.
 */
@Data
@Entity
@EqualsAndHashCode
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSnakeCase
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "image_id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("image_id")
    @JsonSerialize(using = ImageSerialiser.class)
    private Image image;

    @NotNull
    @Column(name = "account_id")
    private String accountId;

    @NotNull
    private String comment;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", image=" + image.getImageId() +
                ", id='" + accountId + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}

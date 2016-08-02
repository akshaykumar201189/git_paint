package com.pied.piper.core.db.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * Created by ankit.c on 22/07/16.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSnakeCase
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@Table(name = "image_likes" ,uniqueConstraints = { @UniqueConstraint( columnNames = { "account_id", "image_id" } ) })
public class ImageLikes extends BaseEntity {

    @NotNull
    @Column(name = "image_id")
    private Long imageId;

    @NotNull
    @Column(name = "account_id")
    private String accountId;
}

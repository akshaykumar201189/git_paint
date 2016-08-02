package com.pied.piper.core.db.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pied.piper.core.enums.EntityStatus;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshay.kesarwan on 21/07/16.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSnakeCase
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Image extends BaseEntity {

    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "num_of_likes")
    private Integer numOfLikes = 0;

    @Column(name = "num_of_comments")
    private Integer numOfComments = 0;

    @Column(name = "is_cloned")
    private Boolean isCloned = false;

    @OneToMany(mappedBy = "sourceImage", fetch = FetchType.LAZY)
    private List<ImageTags> tags = new ArrayList<>();

    @Column(name = "entity_status")
    @Enumerated(EnumType.STRING)
    private EntityStatus entityStatus = EntityStatus.ACTIVE;

    @Transient
    private ImageRelation imageRelation;
}

package com.pied.piper.core.db.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pied.piper.core.db.model.enums.ApprovalStatusEnum;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * Created by akshay.kesarwan on 22/07/16.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSnakeCase
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class ImageRelation extends BaseEntity {

    @Column(name = "source_image")
    private Long sourceImage;

    @Column(name = "cloned_image")
    private Long clonedImage;

    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    private ApprovalStatusEnum approvalStatus;

}

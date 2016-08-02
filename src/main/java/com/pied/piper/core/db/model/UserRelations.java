package com.pied.piper.core.db.model;

import com.fasterxml.jackson.annotation.*;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by palash.v on 21/07/16.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@JsonSnakeCase
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class UserRelations extends BaseEntity {

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "source_user_id")
    @JsonProperty(value = "source_user_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private User sourceUser;

    @Column(name = "destination_user_id")
    @JsonProperty(value = "destination_user_id")
    private Long destinationUserId;
}

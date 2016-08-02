package com.pied.piper.core.db.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by palash.v on 21/07/16.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@JsonSnakeCase
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class User extends BaseEntity {

    @Column(name = "account_id", unique = true)
    private String accountId;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "name")
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "full_profile_url")
    private String fullProfileUrl;

    @OneToMany(mappedBy = "sourceUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserRelations> followers = new ArrayList<>();

}

package com.pied.piper.core.db.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pied.piper.util.JodaDateTimeConverter;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;

/**
 * Created by akshay.kesarwan on 16/08/16.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@JsonSnakeCase
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Session extends BaseEntity {

    @Column(name = "session_id")
    @JsonProperty(value = "session_id")
    private String sessionId;

    @Column(name = "account_id")
    @JsonProperty(value = "account_id")
    private String accountId;

    @Column(name = "expiration_time")
    @JsonProperty(value = "expiration_time")
    @Convert(converter = JodaDateTimeConverter.class)
    private DateTime expirationTime;
}

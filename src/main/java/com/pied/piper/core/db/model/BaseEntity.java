package com.pied.piper.core.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pied.piper.core.db.listeners.BaseEntityListener;
import com.pied.piper.util.JodaDateTimeConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by akshay.kesarwan on 02/08/16.
 */
@Data
@MappedSuperclass
@EntityListeners(BaseEntityListener.class)
@EqualsAndHashCode(of = {"id"})
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JsonProperty(value = "id")
    private Long id;

    @Version
    @Column(name = "version")
    @JsonIgnore
    private Long version;

    @Column(name = "created_at")
    @JsonProperty(value = "created_at")
    @Convert(converter = JodaDateTimeConverter.class)
    private DateTime createdAt;

    @Column(name = "updated_at")
    @JsonProperty(value = "updated_at")
    @Convert(converter = JodaDateTimeConverter.class)
    private DateTime updatedAt;

    public Long getId(){ return id; }
}
package com.pied.piper.core.db.listeners;

import com.pied.piper.core.db.model.BaseEntity;
import org.joda.time.DateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * Created by akshay.kesarwan on 02/08/16.
 */
public class BaseEntityListener {
    @PreUpdate
    public void setUpdatedAt(BaseEntity entity) {
        entity.setUpdatedAt(new DateTime());
    }

    @PrePersist
    public void setCreatedAt(BaseEntity entity) {
        DateTime dateTime = new DateTime();
        entity.setCreatedAt(dateTime);
        entity.setUpdatedAt(dateTime);
    }
}

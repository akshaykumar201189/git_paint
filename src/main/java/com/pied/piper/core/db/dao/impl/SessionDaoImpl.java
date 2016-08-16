package com.pied.piper.core.db.dao.impl;

import com.google.inject.Inject;
import com.pied.piper.core.db.model.Session;

import javax.inject.Provider;
import javax.persistence.EntityManager;

/**
 * Created by akshay.kesarwan on 17/08/16.
 */
public class SessionDaoImpl extends BaseDaoImpl<Session, Long> {
    @Inject
    public SessionDaoImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        this.entityClass = Session.class;
    }
}

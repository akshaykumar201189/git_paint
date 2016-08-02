package com.pied.piper.core.db.dao.impl;

import com.google.inject.Inject;
import com.pied.piper.core.db.dao.api.BaseDao;
import com.pied.piper.core.db.model.UserRelations;

import javax.inject.Provider;
import javax.persistence.EntityManager;

/**
 * Created by akshay.kesarwan on 02/08/16.
 */
public class UserRelationsDaoImpl extends BaseDaoImpl<UserRelations, Long> implements BaseDao<UserRelations, Long> {
    @Inject
    public UserRelationsDaoImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        this.entityClass = UserRelations.class;
    }
}

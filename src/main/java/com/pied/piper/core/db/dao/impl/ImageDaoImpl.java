package com.pied.piper.core.db.dao.impl;

import com.google.inject.Inject;

import com.pied.piper.core.db.model.Image;
import com.pied.piper.core.db.model.User;
import com.pied.piper.core.dto.SearchImageRequestDto;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Provider;
import javax.persistence.EntityManager;

/**
 * Created by akshay.kesarwan on 21/07/16.
 */
public class ImageDaoImpl extends BaseDaoImpl<Image, Long> {

    @Inject
    public ImageDaoImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        this.entityClass = Image.class;
    }

    public List<Image> findByAccountId(String accountId) {
        if (StringUtils.isEmpty(accountId))
            return Collections.EMPTY_LIST;

        Session session = (Session) getEntityManager().getDelegate();
        Criteria criteria = session.createCriteria(Image.class);
        Criterion typeCriterion = Restrictions.eq("accountId", accountId);
        criteria.add(typeCriterion);

        return criteria.list();
    }

    public List<Image> getMetaData(Long imageId, String accountId) {
        boolean flag = false;
        Map<String, Object> queryMap = new LinkedHashMap<>();
        List<String> queryParamList = new ArrayList<>();
        Session session = (Session) getEntityManager().getDelegate();
        StringBuilder hql = new StringBuilder(
                "select i.imageId, i.accountId, i.description, i.numOfLikes, i.title, i.version, i.isCloned " +
                        " from Image i " +
                        " where "
        );

        if (imageId != null) {
            queryParamList.add("i.imageId = :imageId");
            queryMap.put("imageId", imageId);
            flag = true;
        }

        if (StringUtils.isNotEmpty(accountId)) {
            queryParamList.add(" i.accountId = :accountId");
            queryMap.put("accountId", accountId);
            flag = true;
        }

        if (!flag)
            return Collections.EMPTY_LIST;

        hql.append(StringUtils.join(queryParamList, " and "));
        Query query = session.createQuery(hql.toString());
//        for(Map.Entry<String, Object> entry : queryMap.entrySet()){
//            query.setParameter(entry.getKey(), entry.getValue());
//        }

        query.setProperties(queryMap);

        List<Object[]> resultSet = (List<Object[]>) query.list();
        if (resultSet == null || resultSet.size() == 0)
            return Collections.EMPTY_LIST;

        List<Image> images = new ArrayList<>();
        for (Object[] row : resultSet) {
            Image image = new Image();
            image.setImageId((Long) row[0]);
            image.setAccountId((String) row[1]);
            image.setDescription((String) row[2]);
            image.setNumOfLikes((Integer) row[3]);
            image.setTitle((String) row[4]);
            image.setVersion((Integer) row[5]);
            image.setIsCloned((Boolean) row[6]);
            images.add(image);
        }

        return images;
    }

    public List<Image> searchImage(SearchImageRequestDto searchImageDto) {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria criteria = session.createCriteria(User.class);
        if (searchImageDto.getTitle() != null) {
            Criterion typeCriterion = Restrictions.eq("title", searchImageDto.getTitle());
            criteria.add(typeCriterion);
        }
        if (searchImageDto.getAccountId() != null) {
            Criterion accountIdCriteria = Restrictions.eq("accountId", searchImageDto.getAccountId());
            criteria.add(accountIdCriteria);
        }
        return criteria.list();
    }

}

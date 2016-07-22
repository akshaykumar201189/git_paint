package com.pied.piper.core.services.impl;

import com.google.inject.Inject;
import com.pied.piper.core.db.dao.impl.ImageRelationDaoImpl;
import com.pied.piper.core.db.model.ImageRelation;
import com.pied.piper.core.services.interfaces.ImageRelationService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by akshay.kesarwan on 22/07/16.
 */
public class ImageRelationServiceImpl implements ImageRelationService {

    private final ImageRelationDaoImpl imageRelationDao;

    @Inject
    public ImageRelationServiceImpl(ImageRelationDaoImpl imageRelationDao) {
        this.imageRelationDao = imageRelationDao;
    }

    @Override
    public List<ImageRelation> getImageRelationsForSourceImageIds(List<Long> imageIds) {
        Criterion criterion = Restrictions.in("sourceImage", imageIds);
        return imageRelationDao.findByCriteria(criterion);
    }

    @Override
    public List<ImageRelation> getImageRelationsForClonedImageIds(List<Long> imageIds) {
        Criterion criterion = Restrictions.in("clonedImage", imageIds);
        return imageRelationDao.findByCriteria(criterion);
    }

    @Override
    public void saveImageRelation(ImageRelation imageRelation) {
        imageRelationDao.save(imageRelation);
    }

    @Override
    public ImageRelation findById(Long id) {
        if(id == null) return null;
        return imageRelationDao.fetchById(id);
    }
}

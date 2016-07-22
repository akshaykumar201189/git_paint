package com.pied.piper.core.services.interfaces;

import com.google.inject.ImplementedBy;
import com.pied.piper.core.db.model.ImageRelation;
import com.pied.piper.core.services.impl.ImageRelationServiceImpl;

import java.util.List;

/**
 * Created by akshay.kesarwan on 22/07/16.
 */
@ImplementedBy(ImageRelationServiceImpl.class)
public interface ImageRelationService {
    List<ImageRelation> getImageRelationsForSourceImageIds(List<Long> imageIds);
    List<ImageRelation> getImageRelationsForClonedImageIds(List<Long> imageIds);
    void saveImageRelation(ImageRelation imageRelation);
    ImageRelation findById(Long id);
}

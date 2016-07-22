package com.pied.piper.core.services.interfaces;

import com.pied.piper.core.db.model.ImageLikes;
import com.pied.piper.core.dto.CreateImageLikedDto;

import java.util.List;

/**
 * Created by ankit.c on 22/07/16.
 */
public interface ImageLikesService {
    ImageLikes save(Long imageId, String accountId);
    List<ImageLikes> findByImageId(Long imageId);
    List<ImageLikes> findByAccountId(String accountId);
}

package com.pied.piper.core.services.impl;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.pied.piper.core.db.dao.impl.ImageLikesDaoImpl;
import com.pied.piper.core.db.model.Image;
import com.pied.piper.core.db.model.ImageLikes;
import com.pied.piper.core.services.interfaces.GalleriaService;
import com.pied.piper.core.services.interfaces.ImageLikesService;
import com.pied.piper.core.services.interfaces.UserService;
import com.pied.piper.exception.ResponseException;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by ankit.c on 22/07/16.
 */
public class ImageLikesServiceImpl implements ImageLikesService {
    @Inject
    private ImageLikesDaoImpl likeDao;

    @Inject
    private GalleriaService galleriaService;

    @Inject
    private UserService userService;

    @Override
    @Transactional
    public ImageLikes save(Long imageId, String accountId) {
        Image image = galleriaService.getImage(imageId);
        if(image == null) {
            throw new ResponseException("image not found", Response.Status.NOT_FOUND);
        }

        ImageLikes entity = new ImageLikes();
        entity.setImageId(image.getId());
        entity.setAccountId(accountId);
        likeDao.save(entity);
        image.setNumOfLikes(image.getNumOfLikes() + 1);
        return entity;
    }

    @Override
    @Transactional
    public List<ImageLikes> findByImageId(Long imageId) {
        return likeDao.findByImageId(imageId);
    }

    @Override
    @Transactional
    public List<ImageLikes> findByAccountId(String accountId) {
        return likeDao.findByAccountId(accountId);
    }
}

package com.pied.piper.core.services.impl;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.pied.piper.core.db.dao.impl.CommentDaoImpl;
import com.pied.piper.core.db.model.Comment;
import com.pied.piper.core.db.model.Image;
import com.pied.piper.core.db.model.ImageLikes;
import com.pied.piper.core.db.model.User;
import com.pied.piper.core.dto.CreateCommentDto;
import com.pied.piper.core.services.interfaces.CommentService;
import com.pied.piper.core.services.interfaces.GalleriaService;
import com.pied.piper.core.services.interfaces.UserService;
import com.pied.piper.exception.ResponseException;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by ankit.c on 22/07/16.
 */
public class CommentServiceImpl implements CommentService {
    @Inject
    private CommentDaoImpl commentDao;

    @Inject
    private GalleriaService galleriaService;

    @Inject
    private UserService userService;

    @Override
    @Transactional
    public Comment save(Long imageId, CreateCommentDto dto) {
        Image image = galleriaService.getImage(imageId);
        if(image == null) {
            throw new ResponseException("image not found", Response.Status.NOT_FOUND);
        }

        User user = userService.findByAccountId(dto.getAccountId());
        if(user == null) {
            throw new ResponseException("invalid user id " + dto.getAccountId(), Response.Status.BAD_REQUEST);
        }

        Comment comment = new Comment();
        comment.setImage(image);
        comment.setAccountId(user.getAccountId());
        comment.setComment(dto.getComment());
        commentDao.save(comment);
        return comment;
    }

    @Transactional
    public List<Comment> findByImageId(Long imageId){
        return commentDao.findByImageId(imageId);
    }

    @Transactional
    public List<Comment> findByAccountId(String accountId){
        return commentDao.findByAccountId(accountId);
    }
}

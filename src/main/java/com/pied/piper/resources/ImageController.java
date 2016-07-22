package com.pied.piper.resources;

import com.google.inject.Inject;
import com.pied.piper.core.db.model.Comment;
import com.pied.piper.core.db.model.ImageLikes;
import com.pied.piper.core.dto.CreateCommentDto;
import com.pied.piper.core.dto.CreateImageLikedDto;
import com.pied.piper.core.dto.ImageMetaData;
import com.pied.piper.core.services.interfaces.CommentService;
import com.pied.piper.core.services.interfaces.GalleriaService;
import com.pied.piper.core.services.interfaces.ImageLikesService;
import com.pied.piper.exception.ErrorResponse;
import com.pied.piper.exception.ResponseException;
import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by ankit.c on 21/07/16.
 */
@Slf4j
@Path("/image")
@Produces(MediaType.APPLICATION_JSON)
public class ImageController {
    @Inject
    private GalleriaService galleriaService;

    @Inject
    private ImageLikesService imageLikesService;

    @Inject
    private CommentService commentService;

    public ImageController() {
    }

    @GET
    @Path("/{image_id}/data")
    public Response getImageData(@PathParam("image_id") Long imageId) {
        try {
            String imageData = galleriaService.getImageData(imageId);
            if (imageData == null) {
                ErrorResponse error = new ErrorResponse(Response.Status.NOT_FOUND.getStatusCode(), "image not found");
                return Response.status(Response.Status.NOT_FOUND).entity(error).build();
            }
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] imageBytes = decoder.decodeBuffer(imageData);
            return Response.status(Response.Status.OK).entity(imageBytes).header("Content-type","image/png").build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/{image_id}/metadata")
    public Response getImageMetaData(@PathParam("image_id") Long imageId) {
        try {
            ImageMetaData imageMetaData = galleriaService.getImageMetaData(imageId);
            if (imageMetaData == null) {
                ErrorResponse error = new ErrorResponse(Response.Status.NOT_FOUND.getStatusCode(), "image not found");
                return Response.status(Response.Status.NOT_FOUND).entity(error).build();
            }
            return Response.status(Response.Status.OK).entity(imageMetaData).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        }
    }

    @GET
    @Path("/{image_id}/like")
    public Response getImageLikes(@PathParam("image_id") Long imageId) {
        try {
            List<ImageLikes> likes = imageLikesService.findByImageId(imageId);
            return Response.status(Response.Status.OK).entity(likes).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        } catch (Exception e){
            String errorMsg = String.format("Error while fetching comments for image id %d.", imageId);
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

    @GET
    @Path("/{image_id}/comment")
    public Response getImageComments(@PathParam("image_id") Long imageId) {
        try {
            List<Comment> comments = commentService.findByImageId(imageId);
            return Response.status(Response.Status.OK).entity(comments).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        } catch (Exception e){
            String errorMsg = String.format("Error while fetching comments for image id %d.", imageId);
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

    @POST
    @Path("/{image_id}/like")
    public Response imageLikedByUser(@PathParam("image_id") Long imageId,
                                     @HeaderParam("x-account-id") String accountId) {
        try {
            imageLikesService.save(imageId, accountId);
            return Response.status(Response.Status.CREATED).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        } catch (Exception e) {
            String errorMsg = String.format("Error while saving like for image id %d.", imageId);
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

    @POST
    @Path("/{image_id}/comment")
    public Response saveComment(@PathParam("image_id") Long imageId,
                                @HeaderParam("x-account-id") String accountId,
                                @Valid CreateCommentDto dto) {
        try {
            dto.setAccountId(accountId);
            commentService.save(imageId, dto);
            return Response.status(Response.Status.CREATED).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        } catch (Exception e){
            String errorMsg = String.format("Error while saving comment for image id %d.", imageId);
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }
}

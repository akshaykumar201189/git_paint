package com.pied.piper.resources;

import com.google.inject.Inject;
import com.pied.piper.core.db.model.Comment;
import com.pied.piper.core.db.model.Image;
import com.pied.piper.core.db.model.ImageLikes;
import com.pied.piper.core.dto.CreateCommentDto;
import com.pied.piper.core.dto.SaveImageRequestDto;
import com.pied.piper.core.services.interfaces.CommentService;
import com.pied.piper.core.services.interfaces.GalleriaService;
import com.pied.piper.core.services.interfaces.ImageLikesService;
import com.pied.piper.exception.ErrorResponse;
import com.pied.piper.exception.ResponseException;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ankit.c on 21/07/16.
 */
@Slf4j
@Path("/image")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/image", description = "Image APIs")
//TODO: Add session in header for security purposes
public class ImageController {
    @Inject
    private GalleriaService galleriaService;

    @Inject
    private ImageLikesService imageLikesService;

    @Inject
    private CommentService commentService;

    /**
     * Get the details of an Image
     * @param imageId
     * @return
     */
    @GET
    @Path("/details/{image_id}")
    @ApiOperation(value = "Get Image Details", response = Image.class)
    public Response getImageDetails(@PathParam("image_id") Long imageId) {
        try {
            Image image = galleriaService.getImage(imageId);
            if (image == null) {
                ErrorResponse error = new ErrorResponse(Response.Status.NOT_FOUND.getStatusCode(), "image not found");
                return Response.status(Response.Status.NOT_FOUND).entity(error).build();
            }
            return Response.status(Response.Status.OK).entity(image).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        }
    }

    /**
     * Save or Create the image for a User
     * @param saveImageRequestDto
     * @return
     */
    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Save Image", response = HashMap.class)
    public Response saveImage(@ApiParam("saveImageRequestDto") @Valid SaveImageRequestDto saveImageRequestDto) {
        HashMap response = new HashMap();
        try {
            Long imageId = galleriaService.saveImage(saveImageRequestDto);
            response.put("image_id", imageId);
        } catch (Exception e) {
            String errorMsg = String.format("Error while saving image");
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
        return Response.status(Response.Status.OK).entity(response).build();
    }

    /**
     * Clone an existing image
     * @param accountId
     * @param imageId
     * @return
     */
    @POST
    @Path("/clone")
    @ApiOperation(value = "Clone Image", response = HashMap.class)
    public Response cloneImage(@HeaderParam("x-account-id") String accountId, @QueryParam("image_id") Long imageId) {
        HashMap response = new HashMap();
        try {
            Long clonedImageId = galleriaService.cloneImage(imageId, accountId);
            response.put("image_id", clonedImageId);
            return Response.status(200).entity(response).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        } catch (Exception e) {
            String errorMsg = String.format("Error while cloning pull request for image id %d.", imageId);
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

    /**
     * Get the likes for Image
     * @param imageId
     * @return
     */
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

    /**
     * Get the comments for Image
     * @param imageId
     * @return
     */
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

    /**
     * Like an Image
     * @param imageId
     * @param accountId
     * @return
     */
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

    /**
     * Comment an Image
     * @param imageId
     * @param accountId
     * @param dto
     * @return
     */
    @POST
    @Path("/{image_id}/comment")
    public Response saveComment(@PathParam("image_id") Long imageId,
                                @HeaderParam("x-account-id") String accountId,
                                @ApiParam("dto") @Valid CreateCommentDto dto) {
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

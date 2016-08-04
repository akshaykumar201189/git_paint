package com.pied.piper.resources;

import com.google.inject.Inject;
import com.pied.piper.core.db.model.User;
import com.pied.piper.core.dto.ImageMetaData;
import com.pied.piper.core.dto.ProfileDetails;
import com.pied.piper.core.dto.user.SignInRequestDto;
import com.pied.piper.core.dto.user.SignInResponseDto;
import com.pied.piper.core.services.interfaces.GalleriaService;
import com.pied.piper.core.services.interfaces.SessionService;
import com.pied.piper.core.services.interfaces.UserService;
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
import java.util.List;

/**
 * Created by ankit.c on 21/07/16.
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/user", description = "User APIs")
@Slf4j
//TODO: Add session in header for security purposes
public class UserController {
    @Inject
    private UserService userService;
    @Inject
    private GalleriaService galleriaService;
    @Inject
    private SessionService sessionService;

    /**
     * Sign In API for user
     * @param signInRequestDto
     * @return
     */
    @POST
    @Path(("/signIn"))
    @ApiOperation(value = "Sign In User", response = SignInResponseDto.class)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response signInUser(@ApiParam("signInRequestDto") @Valid SignInRequestDto signInRequestDto) {
        try {
            SignInResponseDto responseDto = userService.signInUser(signInRequestDto);
            return Response.status(Response.Status.OK).entity(responseDto).build();
        } catch (Exception e) {
            String errorMsg = String.format("Error while signing for ", signInRequestDto.getUserDetails().getId());
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

    /**
     * Get Profile Details for a User
     * @param accountId
     * @param sessionId
     * @return
     */
    @GET
    @Path("/profile/details/{account_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Profile Details", response = ProfileDetails.class)
    public Response getProfileDetails(@PathParam("account_id") String accountId, @HeaderParam("x-session-id") String sessionId) {
        String ownerAccountId = null;
        try {
            ownerAccountId = sessionService.validateAndGetAccountForSession(sessionId);
        } catch (ResponseException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
        }
        try {
            ProfileDetails profileDetails = galleriaService.getProfileDetails(accountId, ownerAccountId);
            return Response.status(Response.Status.OK).entity(profileDetails).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        } catch (Exception e) {
            String errorMsg = String.format("Error while getting profile details for account id %d.", accountId);
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

    /**
     * Add Follower API
     * @param userAccountId
     * @param followerAccountId
     * @return
     */
    @GET
    @Path("/addFollower")
    @ApiOperation("Add Follower")
    public Response addFollower(@HeaderParam("x-account-id") String userAccountId, @QueryParam("follower_account_id") String followerAccountId) {
        try {
            userService.addFollower(userAccountId, followerAccountId);
            return Response.status(Response.Status.OK).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        } catch (Exception e){
            String errorMsg = String.format("Error while creating followers details for account id %d.", userAccountId);
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

    @GET
    @Path("/home")
    public Response getImagesForFollower(@HeaderParam("x-account-id") String userId) {
        try {
            List<User> followers = userService.getFollowers(userId);
            List<List<ImageMetaData>> followerImages = userService.getFollowerImages(userId);
            if (followerImages == null) {
                ErrorResponse error = new ErrorResponse(Response.Status.NOT_FOUND.getStatusCode(), "follower images not found");
                return Response.status(Response.Status.NOT_FOUND).entity(error).build();
            }
            return Response.status(Response.Status.OK).entity(followerImages).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        } catch (Exception e){
            String errorMsg = String.format("Error while getting follower images details for user id %d.", userId);
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }
}

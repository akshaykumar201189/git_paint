package com.pied.piper.resources;

import com.google.inject.Inject;
import com.pied.piper.core.dto.PullRequest;
import com.pied.piper.core.dto.SaveImageRequestDto;
import com.pied.piper.core.dto.SearchResponseDto;
import com.pied.piper.core.services.interfaces.GalleriaService;
import com.pied.piper.exception.ErrorResponse;
import com.pied.piper.exception.ResponseException;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by akshay.kesarwan on 21/05/16.
 */
@Path("/galleria")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/galleria", description = "Galleria APIs")
//TODO: Add session in header for security purposes
public class GalleriaController {

    private GalleriaService galleriaService;

    @Inject
    public GalleriaController(GalleriaService galleriaService) {
        this.galleriaService = galleriaService;
    }

    /**
     * Searches matching tags and users for a given text
     * @param searchText
     * @return
     */
    @GET
    @Path("/search/any/{txt}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Search Tags and Users", response = SearchResponseDto.class)
    public Response search(@PathParam("txt") String searchText) {
        try {
            SearchResponseDto searchResponseDto = galleriaService.search(searchText);
            return Response.status(Response.Status.OK).entity(searchResponseDto).build();
        } catch (Exception e) {
            String errorMsg = String.format("Error while searching for " + searchText);
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

    /**
     * Send a Pull Request
     * @param accountId
     * @param clonedImageId
     * @return
     */
    @POST
    @Path("/pullrequest/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Send Pull Request")
    public Response sendPullRequest(@HeaderParam("x-account-id") String accountId, @QueryParam("image_id") Long clonedImageId) {
        try {
            galleriaService.sendPullRequest(clonedImageId, accountId);
            return Response.status(Response.Status.OK).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        } catch (Exception e) {
            String errorMsg = String.format("Error while sending pull request for cloned image id %d.", clonedImageId);
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

    /**
     * Approve a Pull Request
     * @param prId
     * @param saveImageRequestDto
     * @return
     */
    @POST
    @Path("/pullrequest/approve/{pr_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Approve Pull Request")
    public Response approvePullRequest(@PathParam("pr_id") Long prId, @ApiParam("saveImageRequestDto") SaveImageRequestDto saveImageRequestDto) {
        try {
            galleriaService.approvePullRequest(prId, saveImageRequestDto);
            return Response.status(Response.Status.OK).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        } catch (Exception e) {
            String errorMsg = String.format("Error while approving pull request for pr id %d.", prId);
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

    /**
     * Reject Pull Request
     * @param prId
     * @return
     */
    @POST
    @Path("/pullrequest/reject/{pr_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Reject Pull Request")
    public Response rejectPullRequest(@PathParam("pr_id") Long prId) {
        try {
            galleriaService.rejectPullRequest(prId);
            return Response.status(Response.Status.OK).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        } catch (Exception e) {
            String errorMsg = String.format("Error while rejecting pull request for pr id %d.", prId);
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

    /**
     * Get Pull Request Details
     * @param prId
     * @return
     */
    @GET
    @Path("/pullrequest/{pr_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Approve Pull Request", response = PullRequest.class)
    public Response getPullRequestDetails(@PathParam("pr_id") Long prId) {
        try {
            return Response.status(Response.Status.OK).entity(galleriaService.getPullRequest(prId)).build();
        } catch (ResponseException e) {
            return Response.status(e.getErrorResponse().getErrorCode()).entity(e.getErrorResponse()).build();
        } catch (Exception e) {
            String errorMsg = String.format("Error while getting pull request for pr id %d.", prId);
            log.error(errorMsg, e);
            ErrorResponse errorResponse = new ErrorResponse(errorMsg + " " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }
}

package com.pied.piper.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.ws.rs.core.Response;

/**
 * Created by ankit.c on 21/07/16.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResponseException extends RuntimeException {
    final private static int serverError = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

    private ErrorResponse ErrorResponse;

    public ResponseException(String message){
        this(message, serverError);
    }

    public ResponseException(String message, Throwable cause){
        this(message, cause, serverError);
    }

    public ResponseException(String message, Response.Status status){
        this(message, status.getStatusCode());
    }

    public ResponseException(String message, int statusCode){
        this(message, null, statusCode);
    }

    public ResponseException(String message, Throwable cause, Response.Status status){
        this(message, cause, status.getStatusCode());
    }

    public ResponseException(String message, Throwable cause, int statusCode){
        this(new ErrorResponse(statusCode, message), cause);
    }

    public ResponseException(ErrorResponse ErrorResponse){
        this(ErrorResponse, null);
    }

    public ResponseException(ErrorResponse ErrorResponse, Throwable cause){
        super(ErrorResponse.getErrorMessage(), cause);
        this.ErrorResponse = ErrorResponse;
    }

    public int getErrorCode(){
        return ErrorResponse.getErrorCode();
    }
}

package com.chiops.gateway.libs.exceptions.handler;

import java.util.stream.Collectors;

import com.chiops.gateway.libs.exceptions.entities.ErrorResponse;
import com.chiops.gateway.libs.exceptions.exception.*;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.*;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;


@Produces(MediaType.APPLICATION_JSON)
@Singleton
@Requires(classes = ExceptionHandler.class)
public class GlobalExceptionHandler
    implements ExceptionHandler<Throwable, HttpResponse<ErrorResponse>> {

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, Throwable exception) {
        HttpStatus status;
        ErrorResponse errorBody;
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException ex = (ConstraintViolationException) exception;
            String msg = ex.getConstraintViolations()
                           .stream()
                           .map(v -> v.getPropertyPath() + " " + v.getMessage())
                           .collect(Collectors.joining("; "));
            ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST, msg, request.getPath());
            return HttpResponse.badRequest(body);
        }
        if (exception instanceof HttpClientResponseException) {
            HttpClientResponseException e = (HttpClientResponseException) exception;
            status = e.getStatus();
            errorBody = e.getResponse()
                         .getBody(ErrorResponse.class)
                         .orElse(new ErrorResponse(status,
                                                   e.getMessage(),
                                                   request.getPath()));
        }
        else if (exception instanceof RuntimeException) {
            RuntimeException rex = (RuntimeException) exception;
            status = determineHttpStatus(rex);
            errorBody = new ErrorResponse(status,
                                          rex.getMessage(),
                                          request.getPath());
        }
        else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorBody = new ErrorResponse(status,
                                          "Unexpected error",
                                          request.getPath());
        }

        return HttpResponse
                .status(status)
                .body(errorBody);
    }

    private HttpStatus determineHttpStatus(RuntimeException ex) {
        if (ex instanceof NotFoundException) return HttpStatus.NOT_FOUND;
        if (ex instanceof ConflictException) return HttpStatus.CONFLICT;
        if (ex instanceof BadRequestException || ex instanceof ParseError) 
            return HttpStatus.BAD_REQUEST;
        if (ex instanceof AuthenticationFailedException || ex instanceof NotAuthenticatedException) 
            return HttpStatus.UNAUTHORIZED;
        if (ex instanceof ConstraintViolationException) return HttpStatus.BAD_REQUEST;
        if (ex instanceof PermissionDeniedException) return HttpStatus.FORBIDDEN;
        if (ex instanceof UnsupportedMediaTypeException) return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        if (ex instanceof InternalServerException){ return HttpStatus.INTERNAL_SERVER_ERROR;}

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
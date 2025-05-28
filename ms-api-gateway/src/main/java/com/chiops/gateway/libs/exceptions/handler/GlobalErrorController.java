package com.chiops.gateway.libs.exceptions.handler;

import com.chiops.gateway.libs.exceptions.entities.ErrorResponse;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;

@Controller  // Global error controller
public class GlobalErrorController {

    @Error(global = true, status = HttpStatus.METHOD_NOT_ALLOWED)
    public HttpResponse<ErrorResponse> handleMethodNotAllowed(HttpRequest<?> request) {
        ErrorResponse err = new ErrorResponse(
            HttpStatus.METHOD_NOT_ALLOWED,
            "Method " + request.getMethod() +
            " not allowed for " + request.getPath(),
            request.getPath()
        );
        return HttpResponse
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(err);
    }

    @Error(global = true, status = HttpStatus.NOT_FOUND)
    public HttpResponse<ErrorResponse> handleNotFound(HttpRequest<?> request) {
        ErrorResponse err = new ErrorResponse(
            HttpStatus.NOT_FOUND,
            "Endpoint " + request.getPath() + request.getBody() +
            " not found or parameter set in the request does not exist",
            request.getPath()
        );
        return HttpResponse
                .status(HttpStatus.NOT_FOUND)
                .body(err);
    }
}

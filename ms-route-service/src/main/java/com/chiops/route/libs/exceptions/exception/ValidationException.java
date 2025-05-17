package com.chiops.route.libs.exceptions.exception;

public class ValidationException extends BadRequestException {
    public ValidationException(String message) {
        super(message);
    }
}

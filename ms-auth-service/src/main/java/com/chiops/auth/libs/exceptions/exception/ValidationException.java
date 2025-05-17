package com.chiops.auth.libs.exceptions.exception;

public class ValidationException extends BadRequestException {
    public ValidationException(String message) {
        super(message);
    }
}

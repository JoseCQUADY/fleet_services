package com.chiops.invitation.libs.exceptions.exception;

public class ValidationException extends BadRequestException {
    public ValidationException(String message) {
        super(message);
    }
}

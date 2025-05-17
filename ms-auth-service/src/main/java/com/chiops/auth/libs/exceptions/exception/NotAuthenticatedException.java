package com.chiops.auth.libs.exceptions.exception;

public class NotAuthenticatedException extends RuntimeException {
    public NotAuthenticatedException(String message) {
        super(message);
    }
}

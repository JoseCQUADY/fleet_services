package com.chiops.route.libs.exceptions.exception;

public class PreconditionFailedException extends RuntimeException {
    public PreconditionFailedException(String message) {
        super(message);
    }
}
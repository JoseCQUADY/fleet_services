package com.chiops.invitation.libs.exceptions.exception;

public class PreconditionFailedException extends RuntimeException {
    public PreconditionFailedException(String message) {
        super(message);
    }
}
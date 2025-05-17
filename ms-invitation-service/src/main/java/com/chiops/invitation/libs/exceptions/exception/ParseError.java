package com.chiops.invitation.libs.exceptions.exception;

public class ParseError extends BadRequestException {
    public ParseError(String message) {
        super(message);
    }
}
package com.chiops.route.libs.exceptions.exception;

public class ParseError extends BadRequestException {
    public ParseError(String message) {
        super(message);
    }
}
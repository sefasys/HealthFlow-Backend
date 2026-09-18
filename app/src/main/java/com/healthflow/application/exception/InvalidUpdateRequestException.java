package com.healthflow.application.exception;

public class InvalidUpdateRequestException extends RuntimeException {
    public InvalidUpdateRequestException(String message) {
        super(message);
    }
}

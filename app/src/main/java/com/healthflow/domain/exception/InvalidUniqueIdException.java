package com.healthflow.domain.exception;

public class InvalidUniqueIdException extends RuntimeException {
    public InvalidUniqueIdException(String message) {
        super(message);
    }
}

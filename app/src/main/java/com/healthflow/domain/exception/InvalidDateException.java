package com.healthflow.domain.exception;

public class InvalidDateException extends DomainValidationException {
    public InvalidDateException(String message) {
        super(message);
    }
}

package com.healthflow.domain.exception;

public class InvalidTimeRangeException extends DomainValidationException {
    public InvalidTimeRangeException(String message) {
        super(message);
    }
}

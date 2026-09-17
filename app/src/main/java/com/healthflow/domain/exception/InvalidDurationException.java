package com.healthflow.domain.exception;

public class InvalidDurationException extends DomainValidationException {
    public InvalidDurationException(String message) {
        super(message);
    }
}

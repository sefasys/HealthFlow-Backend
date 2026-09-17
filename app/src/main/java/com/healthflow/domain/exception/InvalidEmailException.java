package com.healthflow.domain.exception;

public class InvalidEmailException extends DomainValidationException {
    public InvalidEmailException(String message) {
        super(message);
    }
}

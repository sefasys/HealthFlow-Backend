package com.healthflow.domain.exception;

public class InvalidEmployeeIdException extends DomainValidationException {
    public InvalidEmployeeIdException(String message) {
        super(message);
    }
}

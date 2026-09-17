package com.healthflow.domain.exception;

public class InvalidPatientException extends DomainValidationException {
    public InvalidPatientException(String message) {
        super(message);
    }
}

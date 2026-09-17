package com.healthflow.domain.exception;

public class InvalidAppointmentException extends DomainValidationException {
    public InvalidAppointmentException(String message) {
        super(message);
    }
}

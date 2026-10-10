package com.healthflow.domain.exception;

public class AppointmentConflictException extends DomainValidationException {
    public AppointmentConflictException(String message) {
        super(message);
    }
}

package com.healthflow.application.exception;

public class ClinicianAlreadyExistsException extends RuntimeException {
    public ClinicianAlreadyExistsException(String message) {
        super(message);
    }
}

package com.healthflow.exception;

public class ClinicianAlreadyExistsException extends RuntimeException {
    public ClinicianAlreadyExistsException(String message) {
        super(message);
    }
}

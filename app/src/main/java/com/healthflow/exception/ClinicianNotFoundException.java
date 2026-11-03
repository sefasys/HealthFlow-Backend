package com.healthflow.exception;

public class ClinicianNotFoundException extends RuntimeException {
    public ClinicianNotFoundException(String message) {
        super(message);
    }
}

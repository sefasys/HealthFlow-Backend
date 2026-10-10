package com.healthflow.application.exception;

public class ClinicRegistrarAlreadyExistsException extends RuntimeException {
    public ClinicRegistrarAlreadyExistsException(String message) {
        super(message);
    }
}

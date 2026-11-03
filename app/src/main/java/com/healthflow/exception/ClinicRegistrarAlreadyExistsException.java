package com.healthflow.exception;

public class ClinicRegistrarAlreadyExistsException extends RuntimeException {
    public ClinicRegistrarAlreadyExistsException(String message) {
        super(message);
    }
}

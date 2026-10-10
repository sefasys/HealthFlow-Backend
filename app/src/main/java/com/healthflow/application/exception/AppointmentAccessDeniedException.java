package com.healthflow.application.exception;

public class AppointmentAccessDeniedException extends RuntimeException {
    public AppointmentAccessDeniedException(String message) {
        super(message);
    }
}

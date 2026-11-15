package com.healthflow.exception;

public class AppointmentAccessDeniedException extends RuntimeException {
    public AppointmentAccessDeniedException(String message) {
        super(message);
    }
}

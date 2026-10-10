package com.healthflow.domain.exception;

public class IncompatibleUserRoleException extends RuntimeException {
    public IncompatibleUserRoleException(String message) {
        super(message);
    }
}

package com.healthflow.domain.exception;

public class IncompatibleUserRoleException extends DomainValidationException {
    public IncompatibleUserRoleException(String message) {
        super(message);
    }
}

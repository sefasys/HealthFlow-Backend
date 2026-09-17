package com.healthflow.domain.exception;

public class InvalidSlotException extends DomainValidationException {
    public InvalidSlotException(String message) {
        super(message);
    }
}

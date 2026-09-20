package com.healthflow.domain.exception;

public class InvalidUserException extends DomainValidationException {
  public InvalidUserException(String message) {
    super(message);
  }
}

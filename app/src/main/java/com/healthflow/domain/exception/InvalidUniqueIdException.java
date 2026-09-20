package com.healthflow.domain.exception;

public class InvalidUniqueIdException extends DomainValidationException {
  public InvalidUniqueIdException(String message) {
    super(message);
  }
}

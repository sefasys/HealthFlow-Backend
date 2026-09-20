package com.healthflow.domain.exception;

public class InvalidNationalIdException extends DomainValidationException {
  public InvalidNationalIdException(String message) {
    super(message);
  }
}

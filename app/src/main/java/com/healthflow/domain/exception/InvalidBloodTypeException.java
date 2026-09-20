package com.healthflow.domain.exception;

public class InvalidBloodTypeException extends DomainValidationException {
  public InvalidBloodTypeException(String message) {
    super(message);
  }
}

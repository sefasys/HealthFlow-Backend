package com.healthflow.domain.exception;

public class InvalidClinicianException extends DomainValidationException {
  public InvalidClinicianException(String message) {
    super(message);
  }
}

package com.healthflow.domain.exception;

public class InvalidDepartmentException extends DomainValidationException {
  public InvalidDepartmentException(String message) {
    super(message);
  }
}

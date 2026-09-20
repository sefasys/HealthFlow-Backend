package com.healthflow.domain.exception;

public class InvalidStaffException extends DomainValidationException {
  public InvalidStaffException(String message) {
    super(message);
  }
}

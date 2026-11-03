package com.healthflow.exception;

public class PatientAlreadyExistsException extends RuntimeException {

  public PatientAlreadyExistsException(String message) {
    super(message);
  }
}

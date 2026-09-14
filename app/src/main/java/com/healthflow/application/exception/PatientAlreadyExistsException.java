package com.healthflow.application.exception;

public class PatientAlreadyExistsException extends RuntimeException {

  public PatientAlreadyExistsException(String message) {
    super(message);
  }
}

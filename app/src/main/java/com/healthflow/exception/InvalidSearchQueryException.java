package com.healthflow.exception;

public class InvalidSearchQueryException extends RuntimeException {
  public InvalidSearchQueryException(String message) {
    super(message);
  }
}

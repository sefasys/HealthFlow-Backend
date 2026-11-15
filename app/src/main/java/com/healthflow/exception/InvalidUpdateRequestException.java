package com.healthflow.exception;

public class InvalidUpdateRequestException extends RuntimeException {
  public InvalidUpdateRequestException(String message) {
    super(message);
  }
}

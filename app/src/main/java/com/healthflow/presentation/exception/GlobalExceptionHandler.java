package com.healthflow.presentation.exception;

import com.healthflow.application.exception.InvalidSearchQueryException;
import com.healthflow.application.exception.InvalidUpdateRequestException;
import com.healthflow.application.exception.PatientAlreadyExistsException;
import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.domain.exception.DomainValidationException;
import com.healthflow.presentation.dto.patient.ErrorResponseDto;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(PatientAlreadyExistsException.class)
  public ResponseEntity<ErrorResponseDto> handlePatientAlreadyExists( // 409
      PatientAlreadyExistsException exception) {
    ErrorResponseDto responseDto =
        new ErrorResponseDto(
            HttpStatus.CONFLICT.value(),
            "PATIENT_ALREADY_EXISTS",
            exception.getMessage(),
            LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDto);
  }

  @ExceptionHandler(DomainValidationException.class)
  public ResponseEntity<ErrorResponseDto> handleDomainValidationException( // 400
      DomainValidationException exception) {
    ErrorResponseDto responseDto =
        new ErrorResponseDto(
            HttpStatus.BAD_REQUEST.value(),
            "DOMAIN_VALIDATION_ERROR",
            exception.getMessage(),
            LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleGenericError(Exception exception) { // 500
    logger.error("Unexpected error occurred", exception);

    ErrorResponseDto responseDto =
        new ErrorResponseDto(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred.",
            LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
  }

  @ExceptionHandler(PatientNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handlePatientNotFound( // 404
      PatientNotFoundException exception) {
    ErrorResponseDto responseDto =
        new ErrorResponseDto(
            HttpStatus.NOT_FOUND.value(),
            "PATIENT_NOT_FOUND",
            exception.getMessage(),
            LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
  }

  @ExceptionHandler(InvalidSearchQueryException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidSearchQuery( // 400
      InvalidSearchQueryException exception) {
    ErrorResponseDto responseDto =
        new ErrorResponseDto(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_SEARCH_QUERY",
            exception.getMessage(),
            LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
  }

  @ExceptionHandler(InvalidUpdateRequestException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidUpdateRequest(
      InvalidUpdateRequestException exception) {
    ErrorResponseDto responseDto =
        new ErrorResponseDto(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_UPDATE_REQUEST",
            exception.getMessage(),
            LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
  }
}

package com.healthflow.api.handler;

import com.healthflow.dto.common.ErrorResponseDto;
import com.healthflow.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            PatientNotFoundException.class,
            ClinicianNotFoundException.class,
            ClinicRegistrarNotFoundException.class,
            StaffNotFoundException.class,
            UserNotFoundException.class,
            AvailabilityNotFoundException.class,
            AppointmentNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDto> handleNotFound(RuntimeException exception) {
        return createErrorResponse(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            PatientAlreadyExistsException.class,
            StaffAlreadyExistsException.class,
            ClinicianAlreadyExistsException.class,
            ClinicRegistrarAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponseDto> handleAlreadyExists(RuntimeException exception) {
        return createErrorResponse(HttpStatus.CONFLICT, "RESOURCE_ALREADY_EXISTS", exception.getMessage());
    }

    @ExceptionHandler({
            AvailabilityConflictException.class,
            AppointmentConflictException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponseDto> handleConflict(RuntimeException exception) {
        return createErrorResponse(HttpStatus.CONFLICT, "CONFLICT", exception.getMessage());
    }

    @ExceptionHandler({
            InvalidAvailabilityException.class,
            InvalidAppointmentException.class,
            InvalidSearchQueryException.class,
            InvalidUpdateRequestException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBusinessRuleViolation(RuntimeException exception) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", exception.getMessage());
    }

    @ExceptionHandler(AppointmentAccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(AppointmentAccessDeniedException exception) {
        return createErrorResponse(HttpStatus.FORBIDDEN, "ACCESS_DENIED", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return createErrorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponseDto> handleMalformedRequest(Exception exception) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST",
                "The request contains missing or invalid data.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoResource(NoResourceFoundException exception) {
        return createErrorResponse(HttpStatus.NOT_FOUND, "ENDPOINT_NOT_FOUND", "Endpoint not found.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        ErrorResponseDto response = new ErrorResponseDto(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "METHOD_NOT_ALLOWED",
                "This HTTP method is not supported for this endpoint.",
                Instant.now()
        );
        return new ResponseEntity<>(response, exception.getHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException exception) {
        return createErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE",
                "The request content type is not supported.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrity(DataIntegrityViolationException exception) {
        logger.warn("Data integrity violation", exception);
        return createErrorResponse(HttpStatus.CONFLICT, "DATA_CONFLICT",
                "The request conflicts with existing data.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpectedError(Exception exception) {
        logger.error("Unexpected error occurred", exception);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred.");
    }

    private ResponseEntity<ErrorResponseDto> createErrorResponse(HttpStatus status, String code, String message) {
        ErrorResponseDto response = new ErrorResponseDto(status.value(), code, message, Instant.now());
        return ResponseEntity.status(status).body(response);
    }
}
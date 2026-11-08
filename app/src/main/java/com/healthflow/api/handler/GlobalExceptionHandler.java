package com.healthflow.api.handler;

import com.healthflow.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
import java.util.LinkedHashMap;
import java.util.Map;

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
    public ResponseEntity<ProblemDetail> handleNotFound(RuntimeException exception) {
        return problem(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            PatientAlreadyExistsException.class,
            StaffAlreadyExistsException.class,
            ClinicianAlreadyExistsException.class,
            ClinicRegistrarAlreadyExistsException.class
    })
    public ResponseEntity<ProblemDetail> handleAlreadyExists(RuntimeException exception) {
        return problem(HttpStatus.CONFLICT, "RESOURCE_ALREADY_EXISTS", exception.getMessage());
    }

    @ExceptionHandler({
            AvailabilityConflictException.class,
            AppointmentConflictException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ProblemDetail> handleConflict(RuntimeException exception) {
        return problem(HttpStatus.CONFLICT, "CONFLICT", exception.getMessage());
    }

    @ExceptionHandler({
            InvalidAvailabilityException.class,
            InvalidAppointmentException.class,
            InvalidSearchQueryException.class,
            InvalidUpdateRequestException.class
    })
    public ResponseEntity<ProblemDetail> handleBusinessRuleViolation(RuntimeException exception) {
        return problem(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", exception.getMessage());
    }

    @ExceptionHandler(AppointmentAccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AppointmentAccessDeniedException exception) {
        return problem(HttpStatus.FORBIDDEN, "ACCESS_DENIED", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        ResponseEntity<ProblemDetail> response =
                problem(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "One or more fields are invalid.");
        response.getBody().setProperty("errors", fieldErrors);
        return response;
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ProblemDetail> handleMalformedRequest(Exception exception) {
        return problem(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST",
                "The request contains missing or invalid data.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResource(NoResourceFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "ENDPOINT_NOT_FOUND", "Endpoint not found.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        ProblemDetail body = buildProblem(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED",
                "This HTTP method is not supported for this endpoint.");
        return new ResponseEntity<>(body, exception.getHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException exception) {
        return problem(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE",
                "The request content type is not supported.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException exception) {
        logger.warn("Data integrity violation", exception);
        return problem(HttpStatus.CONFLICT, "DATA_CONFLICT", "The request conflicts with existing data.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedError(Exception exception) {
        logger.error("Unexpected error occurred", exception);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred.");
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String code, String detail) {
        return ResponseEntity.status(status).body(buildProblem(status, code, detail));
    }

    private ProblemDetail buildProblem(HttpStatus status, String code, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        problem.setProperty("code", code);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
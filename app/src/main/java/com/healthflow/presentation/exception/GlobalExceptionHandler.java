package com.healthflow.presentation.exception;


import com.healthflow.application.exception.*;
import com.healthflow.domain.exception.AppointmentConflictException;
import com.healthflow.domain.exception.DomainValidationException;
import com.healthflow.domain.exception.IncompatibleUserRoleException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 — Kayıt bulunamadı
    @ExceptionHandler({
            PatientNotFoundException.class,
            ClinicianNotFoundException.class,
            ClinicRegistrarNotFoundException.class,
            AvailabilityNotFoundException.class,
            AppointmentNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<ProblemDetail> handleNotFound(
            RuntimeException exception
    ) {
        return createErrorResponse(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                exception.getMessage()
        );
    }

    // 409 — Tekrarlı kayıt veya mevcut durumla çakışma
    @ExceptionHandler({
            PatientAlreadyExistsException.class,
            ClinicianAlreadyExistsException.class,
            ClinicRegistrarAlreadyExistsException.class,
            UserAlreadyExistsException.class
    })
    public ResponseEntity<ProblemDetail> handleConflict(
            RuntimeException exception
    ) {
        return createErrorResponse(
                HttpStatus.CONFLICT,
                "RESOURCE_ALREADY_EXISTS",
                exception.getMessage()
        );
    }

    @ExceptionHandler(IncompatibleUserRoleException.class)
    public ResponseEntity<ProblemDetail> handleIncompatibleUserRole(
            IncompatibleUserRoleException exception
    ) {
        return createErrorResponse(
                HttpStatus.CONFLICT,
                "INCOMPATIBLE_USER_ROLE",
                exception.getMessage()
        );
    }

    @ExceptionHandler({
            AvailabilityConflictException.class,
            AppointmentConflictException.class
    })
    public ResponseEntity<ProblemDetail> handleSchedulingConflict(
            RuntimeException exception
    ) {
        return createErrorResponse(
                HttpStatus.CONFLICT,
                "SCHEDULING_CONFLICT",
                exception.getMessage()
        );
    }

    // 403 — İşlem için erişim izni yok
    @ExceptionHandler(AppointmentAccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(
            AppointmentAccessDeniedException exception
    ) {
        return createErrorResponse(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                exception.getMessage()
        );
    }


    @ExceptionHandler({
            InvalidSearchQueryException.class,
            InvalidUpdateRequestException.class
    })
    public ResponseEntity<ProblemDetail> handleInvalidApplicationRequest(
            RuntimeException exception
    ) {
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ProblemDetail> handleDomainValidation(
            DomainValidationException exception
    ) {
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "DOMAIN_VALIDATION_ERROR",
                exception.getMessage()
        );
    }

    // 400 — HTTP isteğinin okunması veya doğrulanması sırasında oluşan hatalar
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ProblemDetail> handleInvalidRequest(
            Exception exception
    ) {
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST",
                "The request contains missing or invalid data."
        );
    }

    // 405 — Endpoint bu HTTP metodunu desteklemiyor
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception
    ) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .headers(exception.getHeaders())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(createProblem(
                        HttpStatus.METHOD_NOT_ALLOWED,
                        "METHOD_NOT_ALLOWED",
                        "This HTTP method is not supported for this endpoint."
                ));
    }

    // 415 — Gönderilen içerik türü desteklenmiyor
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException exception
    ) {
        return createErrorResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "UNSUPPORTED_MEDIA_TYPE",
                "The request content type is not supported."
        );
    }

    // 500 — Beklenmeyen uygulama hatası
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedError(
            Exception exception
    ) {
        logger.error("Unexpected error occurred", exception);

        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred."
        );
    }


    private ResponseEntity<ProblemDetail> createErrorResponse(
            HttpStatus status,
            String code,
            String message
    ) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(createProblem(status, code, message));
    }

    private ProblemDetail createProblem(HttpStatus status, String code, String message) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, message);
        problem.setProperty("code", code);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 409 — Veritabanı kısıtına takılan kayıt
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(
            DataIntegrityViolationException exception
    ) {
        return createErrorResponse(
                HttpStatus.CONFLICT,
                "DATA_CONFLICT",
                "The request conflicts with existing data."
        );
    }

    // 409 — Aynı kaydın eşzamanlı güncellenmesi
    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<ProblemDetail> handleConcurrencyFailure(
            ConcurrencyFailureException exception
    ) {
        return createErrorResponse(
                HttpStatus.CONFLICT,
                "CONCURRENT_UPDATE",
                "The resource was changed by another request. Please retry."
        );
    }

    // 404 — Eşleşen endpoint yok
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResourceFound(
            NoResourceFoundException exception
    ) {
        return createErrorResponse(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                "The requested resource could not be found."
        );
    }


}
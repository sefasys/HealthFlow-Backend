package com.healthflow.presentation.exception;

import com.healthflow.application.dto.common.ErrorResponseDto;
import com.healthflow.application.exception.*;
import com.healthflow.domain.exception.AppointmentConflictException;
import com.healthflow.domain.exception.DomainValidationException;
import com.healthflow.domain.exception.IncompatibleUserRoleException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            AppointmentNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDto> handleNotFound(
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
    public ResponseEntity<ErrorResponseDto> handleConflict(
            RuntimeException exception
    ) {
        return createErrorResponse(
                HttpStatus.CONFLICT,
                "RESOURCE_ALREADY_EXISTS",
                exception.getMessage()
        );
    }

    @ExceptionHandler(IncompatibleUserRoleException.class)
    public ResponseEntity<ErrorResponseDto> handleIncompatibleUserRole(
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
    public ResponseEntity<ErrorResponseDto> handleSchedulingConflict(
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
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(
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
    public ResponseEntity<ErrorResponseDto> handleInvalidApplicationRequest(
            RuntimeException exception
    ) {
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleDomainValidation(
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
    public ResponseEntity<ErrorResponseDto> handleInvalidRequest(
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
    public ResponseEntity<ErrorResponseDto> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception
    ) {
        ErrorResponseDto response = new ErrorResponseDto(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "METHOD_NOT_ALLOWED",
                "This HTTP method is not supported for this endpoint.",
                Instant.now()
        );

        return new ResponseEntity<>(
                response,
                exception.getHeaders(),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    // 415 — Gönderilen içerik türü desteklenmiyor
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnsupportedMediaType(
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
    public ResponseEntity<ErrorResponseDto> handleUnexpectedError(
            Exception exception
    ) {
        logger.error("Unexpected error occurred", exception);

        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred."
        );
    }


    private ResponseEntity<ErrorResponseDto> createErrorResponse(
            HttpStatus status,
            String code,
            String message
    ) {
        ErrorResponseDto response = new ErrorResponseDto(
                status.value(),
                code,
                message,
                Instant.now()
        );

        return ResponseEntity.status(status).body(response);
    }
}
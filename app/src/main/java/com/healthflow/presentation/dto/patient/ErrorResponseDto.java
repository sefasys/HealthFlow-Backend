package com.healthflow.presentation.dto.patient;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        int status,
        String code,
        String message,
        LocalDateTime timestamp
) {
}

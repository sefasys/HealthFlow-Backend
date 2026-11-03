package com.healthflow.dto.common;

import java.time.Instant;

public record ErrorResponseDto(
        int status,
        String code,
        String message,
        Instant timestamp
) {
}
package com.healthflow.application.dto.availability;

import jakarta.validation.constraints.NotBlank;

public record RejectAvailabilityRequestDto(@NotBlank String reason) {
}

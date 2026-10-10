package com.healthflow.application.dto.clinicregistrar;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateClinicRegistrarRequestDto(
        @NotNull
        @Pattern(
                regexp = "(?s).*\\S.*",
                message = "must not be blank"
        )
        String email,
        @NotNull  @Pattern(
                regexp = "(?s).*\\S.*",
                message = "must not be blank"
        )
        String phoneNumber) {
}

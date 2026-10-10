package com.healthflow.application.dto.clinician;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UpdateClinicianRequestDto(
        @Email(message = "Email format is invalid.")
        @Pattern(
                regexp = "(?s).*\\S.*",
                message = "Email must not be blank."
        )
        String email,

        @Pattern(
                regexp = "(?s).*\\S.*",
                message = "Phone number must not be blank."
        )
        String phoneNumber
) {
}
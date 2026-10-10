package com.healthflow.application.dto.clinicregistrar;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UpdateClinicRegistrarRequestDto(
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
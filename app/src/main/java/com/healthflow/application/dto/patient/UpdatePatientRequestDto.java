package com.healthflow.application.dto.patient;

import com.healthflow.domain.model.user.patient.BloodType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdatePatientRequestDto(
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
        String phoneNumber,
        BloodType bloodType
)  {}
//Update için yeni response yazılabilir.
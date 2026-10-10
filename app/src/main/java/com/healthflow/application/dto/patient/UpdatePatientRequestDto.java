package com.healthflow.application.dto.patient;

import com.healthflow.domain.model.user.patient.BloodType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdatePatientRequestDto(
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
        String phoneNumber,
        @NotNull
        BloodType bloodType) {}
//Update için yeni response yazılabilir.
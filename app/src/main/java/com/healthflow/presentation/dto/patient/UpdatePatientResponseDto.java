package com.healthflow.presentation.dto.patient;

import com.healthflow.domain.model.user.patient.BloodType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record UpdatePatientResponseDto(
        @NotBlank
                                       UUID id,
        @NotBlank
                                       String name,
        @NotBlank String surname,
        @NotNull
                                       LocalDate birthDate,
        @NotBlank @Email
                                       String email,
        @NotBlank String phoneNumber,
        @NotBlank BloodType bloodType) {}

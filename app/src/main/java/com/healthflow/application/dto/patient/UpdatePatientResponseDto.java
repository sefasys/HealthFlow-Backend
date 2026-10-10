package com.healthflow.application.dto.patient;

import com.healthflow.domain.model.user.patient.BloodType;
import java.time.LocalDate;
import java.util.UUID;

public record UpdatePatientResponseDto(
        UUID id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        String phoneNumber,
        BloodType bloodType
) {
}
package com.healthflow.dto.clinician;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateClinicianResponseDto(
        UUID id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        String phoneNumber
) {
}
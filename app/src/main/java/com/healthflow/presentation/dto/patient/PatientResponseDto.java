package com.healthflow.presentation.dto.patient;

import java.time.LocalDate;
import java.util.UUID;

public record PatientResponseDto(
        UUID id,
        String name,
        String surname,
        LocalDate birthDate
) {
}

package com.healthflow.presentation.dto.patient;

import java.time.LocalDate;

public record PatientResponseDto(
        Long id,
        String name,
        String surname,
        LocalDate birthDate
) {
}

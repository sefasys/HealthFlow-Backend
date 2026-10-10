package com.healthflow.application.dto.clinicregistrar;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateClinicRegistrarResponseDto(
        UUID id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        String phoneNumber
) {
}
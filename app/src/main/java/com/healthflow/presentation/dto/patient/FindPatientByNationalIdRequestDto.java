package com.healthflow.presentation.dto.patient;

import jakarta.validation.constraints.NotBlank;

public record FindPatientByNationalIdRequestDto(
        @NotBlank
        String nationalId
) {
}

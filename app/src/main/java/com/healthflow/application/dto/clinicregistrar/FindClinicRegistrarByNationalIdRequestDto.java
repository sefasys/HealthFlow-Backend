package com.healthflow.application.dto.clinicregistrar;

import jakarta.validation.constraints.NotBlank;

public record FindClinicRegistrarByNationalIdRequestDto(@NotBlank String nationalId) {
}

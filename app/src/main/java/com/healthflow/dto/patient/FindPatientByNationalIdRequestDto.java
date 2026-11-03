package com.healthflow.dto.patient;

import jakarta.validation.constraints.NotBlank;

public record FindPatientByNationalIdRequestDto(@NotBlank String nationalId) {}

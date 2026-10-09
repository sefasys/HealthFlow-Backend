package com.healthflow.application.dto.clinicregistrar;

import com.healthflow.domain.model.user.staff.EmploymentStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ClinicRegistrarResponseDto(UUID id, String name, String surname, LocalDate birthDate, LocalDate hireDate, EmploymentStatus employmentStatus) {
}

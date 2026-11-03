package com.healthflow.dto.clinicregistrar;

import com.healthflow.entity.user.staff.EmploymentStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ClinicRegistrarResponseDto(UUID id, String name, String surname, LocalDate birthDate, LocalDate hireDate, EmploymentStatus employmentStatus) {
}

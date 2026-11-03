package com.healthflow.dto.clinician;

import com.healthflow.entity.user.staff.EmploymentStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ClinicianResponseDto(UUID id, String name, String surname, LocalDate birthDate, LocalDate hireDate, EmploymentStatus employmentStatus) {}

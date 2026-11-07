package com.healthflow.dto.clinicregistrar;

import com.healthflow.entity.user.staff.EmploymentStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ClinicRegistrarResponse(
        UUID id, String name, String surname, String email, String phoneNumber,
        String employeeNumber, LocalDate hireDate, EmploymentStatus employmentStatus
) {}

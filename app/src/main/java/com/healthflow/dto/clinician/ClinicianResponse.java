package com.healthflow.dto.clinician;

import com.healthflow.entity.user.staff.EmploymentStatus;
import com.healthflow.entity.user.staff.Specialty;

import java.time.LocalDate;
import java.util.UUID;

public record ClinicianResponse(UUID id,
                                String name,
                                String surname,
                                String email,
                                String phoneNumber,
                                String employeeNumber,
                                LocalDate hireDate,
                                EmploymentStatus employmentStatus,
                                Specialty specialty,
                                String licenseNumber) {}

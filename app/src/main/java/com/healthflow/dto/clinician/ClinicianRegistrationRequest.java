package com.healthflow.dto.clinician;

import com.healthflow.entity.user.Gender;
import com.healthflow.entity.user.staff.Specialty;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ClinicianRegistrationRequest(
        @NotBlank @Pattern(regexp = "\\d{10}[02468]") String nationalId,
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 50) String surname,
        @NotNull @Past LocalDate birthDate,
        @NotBlank @Email String email,
        @Pattern(regexp = "^\\+?\\d{10,13}$") String phoneNumber,
        Gender gender,

        @NotBlank @Size(max = 20) String employeeNumber,
        @NotNull @PastOrPresent LocalDate hireDate,

        @NotNull Specialty specialty,
        @NotBlank @Size(max = 30) String licenseNumber
) {}

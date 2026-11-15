package com.healthflow.dto.clinicregistrar;

import com.healthflow.entity.user.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ClinicRegistrarRegistrationRequest(
        @NotBlank @Pattern(regexp = "\\d{10}[02468]") String nationalId,
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 50) String surname,
        @NotNull @Past LocalDate birthDate,
        @NotBlank @Email String email,
        @Pattern(regexp = "^\\+?\\d{10,13}$") String phoneNumber,
        Gender gender,
        @NotBlank @Size(max = 20) String employeeNumber,
        @NotNull @PastOrPresent LocalDate hireDate
) {}
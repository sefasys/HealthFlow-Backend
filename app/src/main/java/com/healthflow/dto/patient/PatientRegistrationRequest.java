package com.healthflow.dto.patient;

import com.healthflow.entity.user.Gender;
import com.healthflow.entity.user.patient.BloodType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

public record PatientRegistrationRequest(
        @NotBlank @Pattern(regexp = "\\d{10}[02468]", message = "Invalid national ID")
        String nationalId,

        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 50) String surname,

        @NotNull @Past LocalDate birthDate,

        @NotBlank @Email String email,

        @Pattern(regexp = "^\\+?\\d{10,13}$", message = "Invalid phone number")
        String phoneNumber,

        Gender gender,
        BloodType bloodType,
        Set<String> allergies
        ){}

package com.healthflow.application.dto.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record CreatePatientRequestDto(
    @NotBlank
        String nationalId, // Burada bilerek String olarak yazdık çünkü DTO’da String, domain’de
    // NationalId tercih ederim. Çünkü DTO HTTP modelidir; client düz JSON
    // gönderir. Domain value object’i dış API contract’ına sızdırmayalım.
    @NotBlank String name,
    @NotBlank String surname,
    @NotNull @PastOrPresent LocalDate birthDate,
    @NotBlank @Email String email,
    @NotBlank String phoneNumber) {}

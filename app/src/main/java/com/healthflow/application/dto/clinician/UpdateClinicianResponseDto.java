package com.healthflow.application.dto.clinician;

import com.healthflow.domain.model.user.patient.BloodType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateClinicianResponseDto(@NotBlank
                                         UUID id,
                                         @NotBlank
                                         String name,
                                         @NotBlank String surname,
                                         @NotNull
                                         LocalDate birthDate,
                                         @NotBlank @Email
                                         String email,
                                         @NotBlank String phoneNumber,
                                         @NotBlank BloodType bloodType) {}
// Bu kısım değişebilir sonuçta email ve phone number da sensitive information sayılabilir başka türlü de kullanıcıyı güncelleme hakkında bilgilendirebiliriz.

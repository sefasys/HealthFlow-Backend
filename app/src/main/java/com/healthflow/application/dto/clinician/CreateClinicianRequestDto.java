package com.healthflow.application.dto.clinician;


import com.healthflow.domain.model.user.staff.EmploymentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateClinicianRequestDto(@NotBlank
                                        String nationalId,// Burada bilerek String olarak yazdık çünkü DTO’da String, domain’de
                                        // NationalId tercih ederim. Çünkü DTO HTTP modelidir; client düz JSON
                                        // gönderir. Domain value object’i dış API contract’ına sızdırmayalım.
                                        @NotBlank
                                        String name,
                                        @NotBlank
                                        String surname,
                                        @NotNull
                                        LocalDate birthDate,
                                        @NotBlank
                                        @Email
                                        String email,
                                        @NotBlank
                                        String phoneNumber,
                                        @NotNull
                                        LocalDate hireDate,
                                        @NotNull
                                        EmploymentStatus employmentStatus//Bunda kararsızım
                                        ) {}

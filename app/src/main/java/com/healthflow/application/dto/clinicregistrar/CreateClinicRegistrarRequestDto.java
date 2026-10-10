package com.healthflow.application.dto.clinicregistrar;

import com.healthflow.domain.model.user.staff.EmploymentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record CreateClinicRegistrarRequestDto(@NotBlank
                                              String nationalId,
                                              @NotBlank
                                              String name,
                                              @NotBlank
                                              String surname,
                                              @NotNull
                                              @PastOrPresent
                                              LocalDate birthDate,
                                              @NotBlank
                                              @Email
                                              String email,
                                              @NotBlank
                                              String phoneNumber,
                                              @NotNull
                                              LocalDate hireDate,
                                              @NotNull
                                              EmploymentStatus employmentStatus) {
}

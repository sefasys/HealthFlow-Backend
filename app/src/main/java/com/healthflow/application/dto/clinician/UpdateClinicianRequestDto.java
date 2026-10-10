package com.healthflow.application.dto.clinician;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateClinicianRequestDto(
                                        @NotNull
                                        @Pattern(
                                                regexp = "(?s).*\\S.*",
                                                message = "must not be blank"
                                        )
                                        String email,
                                        @NotNull  @Pattern(
                                                regexp = "(?s).*\\S.*",
                                                message = "must not be blank"
                                        )
                                        String phoneNumber) {}

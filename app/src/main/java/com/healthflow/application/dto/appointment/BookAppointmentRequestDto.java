package com.healthflow.application.dto.appointment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.UUID;

public record BookAppointmentRequestDto(
        @NotNull
        @NotBlank
        UUID availabilityId,
        @NotNull
        @NotBlank
        LocalTime startTime
) {
}
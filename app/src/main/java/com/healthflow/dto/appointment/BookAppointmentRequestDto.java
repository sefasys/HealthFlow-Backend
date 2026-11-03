package com.healthflow.dto.appointment;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.UUID;

public record BookAppointmentRequestDto(
        @NotNull
        UUID availabilityId,
        @NotNull
        LocalTime startTime
) {
}
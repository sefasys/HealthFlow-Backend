package com.healthflow.application.dto.availability;

import com.healthflow.domain.model.appointment.AvailabilityStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AvailabilityResponseDto(@NotNull UUID uniqueId, @NotNull UUID clinicianId, @NotNull LocalDate date, @NotNull LocalTime startTime, @NotNull LocalTime endTime, @NotNull AvailabilityStatus status, String rejectionReason) {
}

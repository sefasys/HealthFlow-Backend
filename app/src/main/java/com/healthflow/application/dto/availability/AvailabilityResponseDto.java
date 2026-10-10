package com.healthflow.application.dto.availability;

import com.healthflow.domain.model.appointment.AvailabilityStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AvailabilityResponseDto(UUID uniqueId, UUID clinicianId, LocalDate date, LocalTime startTime, LocalTime endTime, AvailabilityStatus status, String rejectionReason) {
}

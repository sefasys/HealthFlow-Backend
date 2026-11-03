package com.healthflow.dto.availability;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AvailabilityResponseDto(UUID uniqueId, UUID clinicianId, LocalDate date, LocalTime startTime, LocalTime endTime, AvailabilityStatus status, String rejectionReason) {
}

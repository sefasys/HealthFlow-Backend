package com.healthflow.dto.availability;

import com.healthflow.entity.availability.AvailabilityStatus;
import com.healthflow.entity.availability.SlotDuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record AvailabilityResponse(
        UUID id,
        UUID clinicianId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        SlotDuration slotDuration,
        AvailabilityStatus status
) {}

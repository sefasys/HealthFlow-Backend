package com.healthflow.application.dto.appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentSlotResponseDto(
        UUID availabilityId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {
}
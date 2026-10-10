package com.healthflow.application.dto.appointment;

import java.time.LocalTime;
import java.util.UUID;

public record BookAppointmentRequestDto(
        UUID availabilityId,
        LocalTime startTime
) {
}
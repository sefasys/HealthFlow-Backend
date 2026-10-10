package com.healthflow.application.dto.appointment;

import com.healthflow.domain.model.appointment.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentResponseDto(
        UUID uniqueId,
        UUID availabilityId,
        UUID patientId,
        UUID clinicianId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        AppointmentStatus status
) {
}
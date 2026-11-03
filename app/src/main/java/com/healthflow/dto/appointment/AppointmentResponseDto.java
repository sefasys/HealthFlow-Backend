package com.healthflow.dto.appointment;

import com.healthflow.entity.appointment.AppointmentStatus;

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
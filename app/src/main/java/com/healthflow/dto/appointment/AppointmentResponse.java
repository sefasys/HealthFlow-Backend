package com.healthflow.dto.appointment;

import com.healthflow.entity.appointment.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID availabilityId,
        UUID clinicianId,
        UUID patientId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status
) {}
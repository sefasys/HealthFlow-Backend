package com.healthflow.dto.appointment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookAppointmentRequest(@NotNull UUID patientId) {
}

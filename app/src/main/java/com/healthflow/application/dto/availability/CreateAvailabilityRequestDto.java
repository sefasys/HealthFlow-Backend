package com.healthflow.application.dto.availability;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateAvailabilityRequestDto(@NotNull LocalDate date, @NotNull LocalTime startTime, @NotNull LocalTime endTime) {
}

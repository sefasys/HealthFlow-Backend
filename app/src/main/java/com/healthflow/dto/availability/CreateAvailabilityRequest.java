package com.healthflow.dto.availability;

import com.healthflow.entity.availability.SlotDuration;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record CreateAvailabilityRequest( @NotNull @Future LocalDateTime startTime,
                                         @NotNull LocalDateTime endTime,
                                         @NotNull SlotDuration slotDuration) {
}

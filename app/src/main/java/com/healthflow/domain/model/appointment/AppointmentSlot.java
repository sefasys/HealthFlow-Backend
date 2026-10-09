package com.healthflow.domain.model.appointment;

import com.healthflow.domain.exception.InvalidSlotException;
import java.time.LocalDate;
import java.util.UUID;

public record AppointmentSlot(
    UUID availabilityId, LocalDate date, TimeRange timeRange, SlotStatus status) {
  public AppointmentSlot {
    if (availabilityId == null || date == null || timeRange == null || status == null) {
      throw new InvalidSlotException("Slot fields cannot be null.");
    }
  }

  public boolean isAvailable() { return status == SlotStatus.AVAILABLE; }
}

package com.healthflow.domain.service;

import com.healthflow.domain.exception.InvalidAppointmentException;
import com.healthflow.domain.exception.InvalidAvailabilityException;
import com.healthflow.domain.model.appointment.*;
import java.util.List;
import java.util.UUID;

public class SlotGenerator {
  public List<AppointmentSlot> generateSlots(Availability availability,
      List<Appointment> appointments) {
    if (availability == null) throw new InvalidAvailabilityException("Availability is required.");
    if (appointments == null || appointments.stream().anyMatch(a -> a == null)) {
      throw new InvalidAppointmentException("Appointment context is required and cannot contain null.");
    }
    UUID clinicianUserId = availability.getClinician().getStaff().getUser().getUniqueId();
    return availability.getTimeRange().split(Availability.SLOT_DURATION).stream()
        .map(range -> {
          boolean booked = appointments.stream().anyMatch(a -> a.blocksSlot()
              && a.involvesUser(clinicianUserId)
              && a.getDate().equals(availability.getDate())
              && a.getTimeRange().overlaps(range));
          return new AppointmentSlot(availability.getUniqueId(), availability.getDate(), range,
              booked ? SlotStatus.BOOKED : SlotStatus.AVAILABLE);
        }).toList();
  }
}

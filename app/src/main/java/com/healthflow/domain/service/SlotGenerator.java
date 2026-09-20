package com.healthflow.domain.service;

import com.healthflow.domain.exception.InvalidAppointmentException;
import com.healthflow.domain.exception.InvalidAvailabilityException;
import com.healthflow.domain.exception.InvalidDurationException;
import com.healthflow.domain.exception.InvalidTimeRangeException;
import com.healthflow.domain.model.appointment.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SlotGenerator {
  private Availability availability;
  private Duration slotDuration;
  private List<Appointment> appointments;

  public List<AppointmentSlot> generateSlots(
      Availability availability, Duration slotDuration, List<Appointment> appointments) {
    if (availability == null)
      throw new InvalidAvailabilityException("Availability can not be null");

    if (slotDuration == null) throw new InvalidDurationException("Duration can not be null");

    if (appointments == null) {
      throw new InvalidAppointmentException("Appointments can not be null");
    }

    List<TimeRange> ranges = availability.getTimeRange().split(slotDuration);
    return buildSlots(availability, appointments, ranges);
  }

  private List<AppointmentSlot> buildSlots( // burada mesela exception check yapacak mıyız?
      Availability availability, List<Appointment> appointments, List<TimeRange> ranges) {
    if (availability == null) {
      throw new InvalidAvailabilityException("Availability can not be null.");
    }
    if (appointments == null) {
      throw new InvalidAppointmentException("Appointments list can not be null.");
    }
    if (ranges == null) {
      throw new InvalidTimeRangeException("Time range list can not be null.");
    }

    List<AppointmentSlot> slots = new ArrayList<>();

    for (TimeRange range : ranges) {
      boolean booked = false;
      for (Appointment appointment : appointments) {
        if (appointment.getStatus() == AppointmentStatus.SCHEDULED
            && appointment.getDate().equals(availability.getDate())
            && appointment.getTimeRange().overlaps(range)) {
          booked = true;
          break;
        }
      }
      AppointmentSlot appointmentSlot;
      if (!booked) {
        appointmentSlot = new AppointmentSlot(range, SlotStatus.AVAILABLE);
      } else {
        appointmentSlot = new AppointmentSlot(range, SlotStatus.BOOKED);
      }
      slots.add(appointmentSlot);
    }
    return slots;
  }
}

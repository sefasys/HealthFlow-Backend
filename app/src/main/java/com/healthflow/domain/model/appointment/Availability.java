package com.healthflow.domain.model.appointment;

import com.healthflow.domain.exception.*;
import com.healthflow.domain.model.user.staff.Clinician;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class Availability {

  private final Clinician clinician;
  private final LocalDate date;
  private final TimeRange timeRange;

  public Availability(Clinician clinician, LocalDate date, TimeRange timeRange) {
    if (clinician == null) {
      throw new InvalidClinicianException("Clinician can not be null.");
    }
    if (date == null) {
      throw new InvalidDateException("Date can not be null");
    }
    if (timeRange == null) {
      throw new InvalidTimeRangeException("Time range cen not be null");
    }

    this.clinician = clinician;
    this.date = date;
    this.timeRange = timeRange;
  }

  public List<AppointmentSlot> generateSlots(
      Duration slotDuration, List<Appointment> appointments) {
    if (slotDuration == null) {
      throw new InvalidDurationException("Duration can not be null.");
    }
    if (appointments == null) {
      throw new InvalidAppointmentException("Appointments list can not be null.");
    }

    return timeRange.split(slotDuration).stream()
        .map(
            range -> {
              boolean booked =
                  appointments.stream()
                      .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
                      .anyMatch(a -> a.getDate().equals(date) && a.getTimeRange().overlaps(range));

              return new AppointmentSlot(range, booked ? SlotStatus.BOOKED : SlotStatus.AVAILABLE);
            })
        .toList();
  }

  public Clinician getClinician() {
    return clinician;
  }

  public LocalDate getDate() {
    return date;
  }

  public TimeRange getTimeRange() {
    return timeRange;
  }
}

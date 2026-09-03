package com.healthflow.domain.model.appointment;

import com.healthflow.domain.model.user.staff.Clinician;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class Availability {

  private final Clinician clinician;
  private final LocalDate date;
  private final TimeRange timeRange;

  public Availability(Clinician clinician, LocalDate date, TimeRange timeRange) {
    this.clinician = clinician;
    this.date = date;
    this.timeRange = timeRange;
  }

  public List<AppointmentSlot> generateSlots(
      Duration slotDuration, List<Appointment> appointments) {
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

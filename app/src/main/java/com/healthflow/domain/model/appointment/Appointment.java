package com.healthflow.domain.model.appointment;

import com.healthflow.domain.exception.InvalidAppointmentException;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.model.user.staff.Clinician;
import java.time.LocalDate;
import java.util.UUID;

public class Appointment {
  private final UUID uniqueId;
  private final UUID availabilityId;
  private final Patient patient;
  private final Clinician clinician;
  private final LocalDate date;
  private final TimeRange timeRange;
  private AppointmentStatus status;

  public Appointment(UUID uniqueId, Patient patient, Availability availability, TimeRange range) {
    this(uniqueId, patient, requireAvailability(availability).getClinician(),
        availability.getUniqueId(), availability.getDate(), range, AppointmentStatus.SCHEDULED);
    if (!availability.accepts(range)) {
      throw new InvalidAppointmentException("Requested range must match one availability slot.");
    }
  }

  private Appointment(UUID uniqueId, Patient patient, Clinician clinician, UUID availabilityId,
      LocalDate date, TimeRange timeRange, AppointmentStatus status) {
    if (uniqueId == null || patient == null || clinician == null || availabilityId == null
        || date == null || timeRange == null || status == null) {
      throw new InvalidAppointmentException("Appointment fields cannot be null.");
    }
    if (!timeRange.duration().equals(Availability.SLOT_DURATION)
        || timeRange.start().getSecond() != 0 || timeRange.start().getNano() != 0) {
      throw new InvalidAppointmentException("Appointment must be a whole 15-minute slot.");
    }
    if (patient.getUser().getUniqueId().equals(clinician.getStaff().getUser().getUniqueId())) {
      throw new InvalidAppointmentException("A clinician cannot be the patient in the same appointment.");
    }
    this.uniqueId = uniqueId;
    this.patient = patient;
    this.clinician = clinician;
    this.availabilityId = availabilityId;
    this.date = date;
    this.timeRange = timeRange;
    this.status = status;
  }

  public static Appointment restore(UUID id, Patient patient, Clinician clinician,
      UUID availabilityId, LocalDate date, TimeRange range, AppointmentStatus status) {
    return new Appointment(id, patient, clinician, availabilityId, date, range, status);
  }

  public boolean involvesUser(UUID userId) {
    return patient.getUser().getUniqueId().equals(userId)
        || clinician.getStaff().getUser().getUniqueId().equals(userId);
  }

  public boolean blocksSlot() { return status != AppointmentStatus.CANCELLED; }

  public void cancel() {
    if (status == AppointmentStatus.CANCELLED) return;
    requireScheduled();
    status = AppointmentStatus.CANCELLED;
  }

  public void complete() {
    requireScheduled();
    status = AppointmentStatus.COMPLETED;
  }

  public void markAsNoShow() {
    requireScheduled();
    status = AppointmentStatus.NO_SHOW;
  }

  private void requireScheduled() {
    if (status != AppointmentStatus.SCHEDULED) {
      throw new InvalidAppointmentException("Only a scheduled appointment can change state.");
    }
  }

  private static Availability requireAvailability(Availability availability) {
    if (availability == null) throw new InvalidAppointmentException("Availability is required.");
    return availability;
  }

  public UUID getUniqueId() { return uniqueId; }
  public UUID getAvailabilityId() { return availabilityId; }
  public Patient getPatient() { return patient; }
  public Clinician getClinician() { return clinician; }
  public LocalDate getDate() { return date; }
  public TimeRange getTimeRange() { return timeRange; }
  public AppointmentStatus getStatus() { return status; }
}

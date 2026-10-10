package com.healthflow.domain.service;

import com.healthflow.domain.exception.AppointmentConflictException;
import com.healthflow.domain.exception.InvalidAppointmentException;
import com.healthflow.domain.exception.InvalidAvailabilityException;
import com.healthflow.domain.model.appointment.*;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.model.user.staff.EmploymentStatus;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Component
public class AppointmentScheduler {
  public static final ZoneId BUSINESS_ZONE = ZoneId.of("Europe/Istanbul");
  private final Clock clock;

  public AppointmentScheduler() { this(Clock.system(BUSINESS_ZONE)); }

  public AppointmentScheduler(Clock clock) {
    if (clock == null) throw new IllegalArgumentException("Clock is required.");
    this.clock = clock.withZone(BUSINESS_ZONE);
  }

  /** The application resolves patient from the authenticated account.
   * existing must include both participants' appointments in either role.
   */
  public Appointment bookAppointment(UUID id, Patient patient, Availability availability,
      TimeRange range, List<Appointment> existing) {
    validateContext(existing);
    if (existing.stream().anyMatch(a -> a.getUniqueId().equals(id))) {
      throw new InvalidAppointmentException("Appointment ID already exists.");
    }
    Appointment candidate = new Appointment(id, patient, availability, range);
    validateBooking(candidate, existing);
    return candidate;
  }

  /** Returns a replacement with the same ID; caller must save only after success. */
  public Appointment reschedule(Appointment current, Availability newAvailability,
      TimeRange newRange, List<Appointment> existing) {
    if (current == null || current.getStatus() != AppointmentStatus.SCHEDULED) {
      throw new InvalidAppointmentException("Only scheduled appointments can be rescheduled.");
    }
    validateContext(existing);
    Appointment candidate = new Appointment(current.getUniqueId(), current.getPatient(),
        newAvailability, newRange);
    List<Appointment> others = existing.stream()
        .filter(a -> !a.getUniqueId().equals(current.getUniqueId())).toList();
    validateBooking(candidate, others);
    return candidate;
  }

  private void validateBooking(Appointment candidate, List<Appointment> existing) {
    if (candidate.getClinician().getStaff().getEmploymentStatus() != EmploymentStatus.ACTIVE) {
      throw new InvalidAvailabilityException("Clinician must be active to receive appointments.");
    }
    if (!LocalDateTime.of(candidate.getDate(), candidate.getTimeRange().start())
        .isAfter(LocalDateTime.now(clock))) {
      throw new InvalidAppointmentException("Appointment start must be in the future.");
    }
    UUID patientId = candidate.getPatient().getUser().getUniqueId();
    UUID clinicianId = candidate.getClinician().getStaff().getUser().getUniqueId();
    for (Appointment other : existing) {
      if (other.blocksSlot() && other.getDate().equals(candidate.getDate())
          && other.getTimeRange().overlaps(candidate.getTimeRange())
          && (other.involvesUser(patientId) || other.involvesUser(clinicianId))) {
        throw new AppointmentConflictException("Patient or clinician has an overlapping appointment.");
      }
    }
  }

  private void validateContext(List<Appointment> existing) {
    if (existing == null || existing.stream().anyMatch(a -> a == null)) {
      throw new InvalidAppointmentException("Appointment context cannot be null or contain null.");
    }
  }
}

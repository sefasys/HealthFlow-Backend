package com.healthflow.appointment;

import com.healthflow.user.patient.Patient;
import com.healthflow.user.staff.Clinician;
import java.util.List;

public class AppointmentScheduler {

  public Appointment schedule(
      Patient patient,
      Clinician clinician,
      Availability availability,
      TimeRange requestedRange,
      List<Appointment> appointments) {

    if (patient == null) throw new IllegalArgumentException("Patient information can not be null.");
    if (clinician == null)
      throw new IllegalArgumentException("Clinician information can not be null.");
    if (availability == null)
      throw new IllegalArgumentException("Availability information can not be null.");
    if (requestedRange == null)
      throw new IllegalArgumentException("Requested range information can not be null.");
    if (appointments == null)
      throw new IllegalArgumentException("Appointments information can not be null.");
    if (!availability.getClinician().equals(clinician)) {
      throw new IllegalArgumentException(
          "Given availability is not matching with the clinician's.");
    }
    if (!availability.getTimeRange().contains(requestedRange)) {
      throw new IllegalArgumentException("Availability time is not matching.");
    }
    for (Appointment appointment : appointments) {
      if (appointment.getClinician().equals(clinician)
          && availability.getDate().equals(appointment.getDate())
          && appointment.getStatus() == AppointmentStatus.SCHEDULED
          && appointment.getTimeRange().overlaps(requestedRange)) {
        throw new IllegalArgumentException("Slot is already booked");
      }
    }
    return new Appointment(
        patient, clinician, availability.getDate(), requestedRange, clinician.getDepartment());
    // ileride bu clinician getDepartment'ı çıkar. constructordan
  }
}

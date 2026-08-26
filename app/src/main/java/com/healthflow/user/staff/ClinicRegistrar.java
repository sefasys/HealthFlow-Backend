package com.healthflow.user.staff;

import com.healthflow.appointment.*;
import com.healthflow.user.patient.Patient;
import java.util.List;

public class ClinicRegistrar {
  private Staff staff;
  private AppointmentScheduler appointmentScheduler;

  public ClinicRegistrar(Staff staff, AppointmentScheduler appointmentScheduler) {
    this.staff = staff;
    this.appointmentScheduler = appointmentScheduler;
  }

  public Appointment createAppointment(
      Patient patient,
      Clinician clinician,
      Availability availability,
      TimeRange requestedRange,
      List<Appointment> appointments) {

    Appointment appointment =
        appointmentScheduler.schedule(
            patient, clinician, availability, requestedRange, appointments);
    appointments.add(appointment);
    patient.addAppointment(appointment);
    return appointment;
  }

  public boolean cancelAppointment(Appointment appointment, List<Appointment> appointments) {
    if (appointment == null) throw new IllegalArgumentException("Appointment is null");
    if (appointments == null) throw new IllegalArgumentException("Appointment list is null");

    if (!appointments.contains(appointment)) {
      return false;
    }
    appointment.cancel();
    return true;
  }

  public void updateAppointment(
      Appointment oldAppointment,
      Availability newAvailability,
      TimeRange newRequestedRange,
      List<Appointment> appointments) {
    if (oldAppointment == null) throw new IllegalArgumentException("Old appointment is null");
    if (newAvailability == null) throw new IllegalArgumentException("New appointment is null");
    if (appointments == null)
      throw new IllegalArgumentException("Appointment list can not be null");
    if (newRequestedRange == null) throw new IllegalArgumentException("Time range can not be null");

    if (!appointments.contains(oldAppointment)) {
      throw new IllegalArgumentException("Old appointment is not on the list.");
    }

    if (oldAppointment.getStatus() != AppointmentStatus.SCHEDULED) {
      throw new IllegalArgumentException("Old appointment is not scheduled.");
    }

    int index = appointments.indexOf(oldAppointment);
    List<Appointment> filteredAppointments =
        appointments.stream().filter(appointment -> !appointment.equals(oldAppointment)).toList();
    Appointment newAppointment =
        appointmentScheduler.schedule(
            oldAppointment.getPatient(),
            oldAppointment.getClinician(),
            newAvailability,
            newRequestedRange,
            filteredAppointments);

    appointments.set(index, newAppointment);
    oldAppointment.getPatient().updateAppointment(oldAppointment, newAppointment);
  }
}

package com.healthflow.domain.model.user.staff;

import com.healthflow.domain.exception.*;
import com.healthflow.domain.model.appointment.*;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.service.AppointmentScheduler;
import java.util.List;
import java.util.UUID;

public class ClinicRegistrar {
  private final Staff staff;
  private final AppointmentScheduler appointmentScheduler;

  public ClinicRegistrar(Staff staff, AppointmentScheduler appointmentScheduler) {
    if(staff == null)
      throw new InvalidStaffException("Staff can not be null.");

    this.staff = staff;
    this.appointmentScheduler = appointmentScheduler;
  }

  public Appointment createAppointment(
      UUID uniqueId,
      Patient patient,
      Clinician clinician,
      Availability availability,
      TimeRange requestedRange,
      List<Appointment> appointments) {
    if (uniqueId == null){
      throw new InvalidUniqueIdException("Unique Id can not be null.");
    }
    if (patient == null){
      throw new InvalidPatientException("Patient can not be null.");
    }
    if (clinician == null){
      throw new InvalidClinicianException("Clinician can not be null.");
    }
    if (availability == null){
      throw new InvalidAvailabilityException("Availability can not be null.");
    }
    if (requestedRange == null){
      throw new InvalidTimeRangeException("Time Range can not be null.");
    }
    if (appointments == null){
      throw new InvalidAppointmentException("Appointment list can not be null.");
    }


    Appointment appointment =
        appointmentScheduler.schedule(
            uniqueId, patient, clinician, availability, requestedRange, appointments);
    appointments.add(appointment);
    patient.addAppointment(appointment);
    return appointment;
  }

  public boolean cancelAppointment(Appointment appointment, List<Appointment> appointments) {
    if (appointment == null) throw new InvalidAppointmentException("Appointment is null");
    if (appointments == null) throw new InvalidAppointmentException("Appointment list is null");

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
    if (oldAppointment == null) throw new InvalidAppointmentException("Old appointment is null");
    if (newAvailability == null) throw new InvalidAvailabilityException("New availability is null");
    if (appointments == null)
      throw new InvalidAppointmentException("Appointments list can not be null");
    if (newRequestedRange == null) throw new InvalidTimeRangeException("Time range can not be null");

    if (!appointments.contains(oldAppointment)) {
      throw new InvalidAppointmentException("Old appointment is not on the list.");
    }

    if (oldAppointment.getStatus() != AppointmentStatus.SCHEDULED) {
      throw new InvalidAppointmentException("Old appointment is not scheduled.");
    }

    int index = appointments.indexOf(oldAppointment);
    List<Appointment> filteredAppointments =
        appointments.stream().filter(appointment -> !appointment.equals(oldAppointment)).toList();
    Appointment newAppointment =
        appointmentScheduler.schedule(
            oldAppointment.getUniqueId(),
            oldAppointment.getPatient(),
            oldAppointment.getClinician(),
            newAvailability,
            newRequestedRange,
            filteredAppointments);

    appointments.set(index, newAppointment);
    oldAppointment.getPatient().updateAppointment(oldAppointment, newAppointment);
  }
}

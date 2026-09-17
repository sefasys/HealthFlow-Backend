package com.healthflow.domain.model.user.patient;

import com.healthflow.domain.exception.InvalidAppointmentException;
import com.healthflow.domain.exception.InvalidBloodTypeException;
import com.healthflow.domain.exception.InvalidPatientException;
import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Patient {
  private final User user;
  private BloodType bloodType;
  private final List<Appointment> appointments = new ArrayList<>();

  public Patient(User user) {

    if (user == null) {
      throw new InvalidPatientException("User must be set.");
    }

    this.user = user;
    bloodType = BloodType.UNKNOWN;
  }

  public User getUser() {
    return user;
  }

  public void updateBloodType(BloodType bloodType) {
    if (bloodType == null) throw new InvalidBloodTypeException("Bloodtype can not be null");

    this.bloodType = bloodType;
  }

  public BloodType getBloodType() {
    return bloodType;
  }

  public List<Appointment> getAppointments() {
    return appointments;
  }

  public void addAppointment(Appointment appointment) {
    if (appointment == null) {
      throw new InvalidAppointmentException("Appointment can not be null.");
    } else if (appointment.getPatient() != this) {
      throw new InvalidAppointmentException("The appointment is not for this patient.");
    } else {
      appointments.add(appointment);
    }
  }

  public boolean removeAppointment(
      Appointment appointment) { // burada appointment return etme. remove metodu zaten boolean
    if (appointment == null) {
      throw new InvalidAppointmentException("Appointment can not be null.");
    } else if (appointment.getPatient() != this) {
      throw new InvalidAppointmentException("The appointment is not for this patient.");
    } else {
      return appointments.remove(appointment);
    }
  }

  public void updateAppointment(Appointment oldAppointment, Appointment newAppointment) {
    if (oldAppointment == null)
      throw new InvalidAppointmentException("Old Appointment can not be null");
    if (newAppointment == null)
      throw new InvalidAppointmentException("New Appointment can not be null");
    if (!appointments.contains(oldAppointment))
      throw new InvalidAppointmentException("Old appointment is not in the list.");
    if (!(oldAppointment.getPatient() == this)) {
      throw new InvalidAppointmentException("Old appointment does not belong to the patient.");
    }
    if (!(newAppointment.getPatient() == this)) {
      throw new InvalidAppointmentException("New appointment does not belong to the patient.");
    }
    int index = appointments.indexOf(oldAppointment);
    appointments.set(index, newAppointment);
  }

  public void updateEmail(String email){
    user.updateEmail(email);
  }
  public void updatePhoneNumber(String phoneNumber){
    user.updatePhoneNumber(phoneNumber);
  }

}

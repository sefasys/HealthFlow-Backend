package com.healthflow.domain.model.user.patient;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.user.User;
import java.util.List;

public class Patient {
  private final User user;
  private final BloodType bloodType;
  private final List<Appointment> appointments;

  public Patient(User user, BloodType bloodType, List<Appointment> appointments) {

    if (user == null) {
      throw new IllegalArgumentException("User must be set.");
    }
    if (bloodType == null) {
      throw new IllegalArgumentException("Blood type must be set.");
    }
    if (appointments == null) {
      throw new IllegalArgumentException("Appointments must be set.");
    }

    this.appointments = appointments;
    this.user = user;
    this.bloodType = bloodType;
  }

  public User getUser() {
    return user;
  }

  public BloodType getBloodType() {
    return bloodType;
  }

  public List<Appointment> getAppointments() {
    return appointments;
  }

  public void addAppointment(Appointment appointment) {
    if (appointment == null) {
      throw new IllegalArgumentException("Appointment can not be null.");
    } else if (appointment.getPatient() != this) {
      throw new IllegalArgumentException("The appointment is not for this patient.");
    } else {
      appointments.add(appointment);
    }
  }

  public boolean removeAppointment(
      Appointment appointment) { // burada appointment return etme. remove metodu zaten boolean
    if (appointment == null) {
      throw new IllegalArgumentException("Appointment can not be null.");
    } else if (appointment.getPatient() != this) {
      throw new IllegalArgumentException("The appointment is not for this patient.");
    } else {
      return appointments.remove(appointment);
    }
  }

  public void updateAppointment(Appointment oldAppointment, Appointment newAppointment) {
    if (oldAppointment == null)
      throw new IllegalArgumentException("Old Appointment can not be null");
    if (newAppointment == null)
      throw new IllegalArgumentException("New Appointment can not be null");
    if (!appointments.contains(oldAppointment))
      throw new IllegalArgumentException("Old appointment is not in the list.");
    if (!(oldAppointment.getPatient() == this)) {
      throw new IllegalArgumentException("Old appointment does not belong to the patient.");
    }
    if (!(newAppointment.getPatient() == this)) {
      throw new IllegalArgumentException("New appointment does not belong to the patient.");
    }
    int index = appointments.indexOf(oldAppointment);
    appointments.set(index, newAppointment);
  }
}

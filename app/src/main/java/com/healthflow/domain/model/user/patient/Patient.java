package com.healthflow.domain.model.user.patient;

import com.healthflow.domain.exception.InvalidAppointmentException;
import com.healthflow.domain.exception.InvalidBloodTypeException;
import com.healthflow.domain.exception.InvalidPatientException;
import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.user.User;
import java.util.ArrayList;
import java.util.List;

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





}

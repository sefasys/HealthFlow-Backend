package com.healthflow.domain.model.user.patient;

import com.healthflow.domain.exception.InvalidBloodTypeException;
import com.healthflow.domain.exception.InvalidPatientException;
import com.healthflow.domain.model.user.User;

public class Patient {
  private final User user;
  private BloodType bloodType;

  public Patient(User user) { this(user, BloodType.UNKNOWN); }

  public Patient(User user, BloodType bloodType) {
    if (user == null) throw new InvalidPatientException("User must be set.");
    if (bloodType == null) throw new InvalidBloodTypeException("Blood type cannot be null.");
    this.user = user;
    this.bloodType = bloodType;
  }

  public User getUser() { return user; }
  public BloodType getBloodType() { return bloodType; }

  public void updateBloodType(BloodType bloodType) {
    if (bloodType == null) throw new InvalidBloodTypeException("Blood type cannot be null.");
    this.bloodType = bloodType;
  }
}

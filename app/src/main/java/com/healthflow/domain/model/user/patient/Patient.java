package com.healthflow.domain.model.user.patient;

import com.healthflow.domain.exception.InvalidBloodTypeException;
import com.healthflow.domain.exception.InvalidPatientException;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;

import java.time.LocalDate;
import java.util.UUID;

/** Patient profile. Appointments are queried through a repository. */
public class Patient {
  private final User user;
  private BloodType bloodType;

    public static Patient register(User user) {
        return new Patient(user, BloodType.UNKNOWN);
    }

  public Patient(User user, BloodType bloodType) {
    if (user == null) throw new InvalidPatientException("User must be set.");
    if (bloodType == null) throw new InvalidBloodTypeException("Blood type cannot be null.");
    this.user = user;
    this.bloodType = bloodType;
  }

  @Deprecated
  public User getUser() { return user; }
  public BloodType getBloodType() { return bloodType; }



  public void updateBloodType(BloodType bloodType) {
    if (bloodType == null) throw new InvalidBloodTypeException("Blood type cannot be null.");
    this.bloodType = bloodType;
  }

    public UUID getUniqueId()          { return user.getUniqueId(); }
    public NationalId getNationalId()  { return user.getNationalId(); }
    public String getName()            { return user.getName(); }
    public String getSurname()         { return user.getSurname(); }
    public LocalDate getBirthDate()    { return user.getBirthDate(); }
    public String getEmail()           { return user.getEmail(); }
    public String getPhoneNumber()     { return user.getPhoneNumber(); }
}

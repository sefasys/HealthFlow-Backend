package com.healthflow.domain.model.user;

import java.time.LocalDate;
import java.util.List;

public class User {

  private final Long uniqueID;
  private final NationalId nationalId;
  private final String name;
  private final String surname;
  private final LocalDate birthDate;
  private final String email;
  private final String phoneNumber;
  private final List<UserRole> userRoleList;

  public User(
      Long uniqueID,
      NationalId nationalId,
      String name,
      String surname,
      LocalDate birthDate,
      String email,
      String phoneNumber,
      List<UserRole> userRoleList) {
    if (uniqueID == null) {
      throw new IllegalArgumentException("uniqueId cannot be null.");
    }
    if (nationalId == null) {
      throw new IllegalArgumentException("National ID cannot be null.");
    }
    if (name == null) {
      throw new IllegalArgumentException("Name cannot be null.");
    }
    if (birthDate == null) {
      throw new IllegalArgumentException("Birthdate cannot be null.");
    }
    if (surname == null) {
      throw new IllegalArgumentException("Surname cannot be null.");
    }
    if (email == null) {
      throw new IllegalArgumentException("E-mail cannot be null.");
    }
    if (phoneNumber == null) {
      throw new IllegalArgumentException("Phone number cannot be null.");
    }
    if (userRoleList == null) {
      throw new IllegalArgumentException("User roles cannot be null.");
    }

    this.uniqueID = uniqueID;
    this.nationalId = nationalId;
    this.name = name;
    this.surname = surname;
    this.birthDate = birthDate;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.userRoleList = userRoleList;
  }

  public Long getUniqueID() {
    return uniqueID;
  }

  public NationalId getNationalId() {
    return nationalId;
  }

  public String getSurname() {
    return surname;
  }

  public String getName() {
    return name;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public String getEmail() {
    return email;
  }

  public List<UserRole> getUserRoleList() {
    return userRoleList;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }
}

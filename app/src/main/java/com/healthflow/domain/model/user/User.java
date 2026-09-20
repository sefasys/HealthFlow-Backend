package com.healthflow.domain.model.user;

import com.healthflow.domain.exception.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class User {

  private final UUID uniqueId;
  private final NationalId nationalId;
  private final String name;
  private final String surname;
  private final LocalDate birthDate;
  private String email;
  private String phoneNumber;
  private final List<UserRole> userRoleList;

  public User(
      UUID uniqueId,
      NationalId nationalId,
      String name,
      String surname,
      LocalDate birthDate,
      String email,
      String phoneNumber,
      List<UserRole> userRoleList) {
    if (uniqueId == null) {
      throw new InvalidUniqueIdException("uniqueId cannot be null.");
    }
    if (nationalId == null) {
      throw new InvalidNationalIdException("National ID cannot be null.");
    }
    if (name == null) {
      throw new InvalidUserException("Name cannot be null.");
    }
    if (birthDate == null) {
      throw new InvalidUserException("Birthdate cannot be null.");
    }
    if (surname == null) {
      throw new InvalidUserException("Surname cannot be null.");
    }
    if (email == null) {
      throw new InvalidEmailException("E-mail cannot be null.");
    }
    if (phoneNumber == null) {
      throw new InvalidPhoneNumberException("Phone number cannot be null.");
    }
    if (userRoleList == null) {
      throw new InvalidUserException("User roles cannot be null.");
    }

    this.uniqueId = uniqueId;
    this.nationalId = nationalId;
    this.name = name;
    this.surname = surname;
    this.birthDate = birthDate;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.userRoleList = userRoleList;
  }

  public UUID getUniqueId() {
    return uniqueId;
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

  public void setUserRole(UserRole role) {
    userRoleList.add(role);
  }

  public List<UserRole> getUserRoleList() {
    return userRoleList;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void updateEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new InvalidEmailException("Email can not be null or blank.");
    }
    this.email = email;
  }

  public void updatePhoneNumber(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.isBlank()) {
      throw new InvalidPhoneNumberException("Phone number can not be null or blank.");
    }
    this.phoneNumber = phoneNumber;
  }
}

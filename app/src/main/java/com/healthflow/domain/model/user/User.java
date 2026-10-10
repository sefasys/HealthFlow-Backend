package com.healthflow.domain.model.user;

import com.healthflow.domain.exception.*;
import java.time.LocalDate;
import java.util.ArrayList;
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
      this.userRoleList = new ArrayList<>();

      for (UserRole role : userRoleList) {
          addRole(role);
      }
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

    public void addRole(UserRole role) {
        if (role == null) {
            throw new InvalidUserException("User role cannot be null.");
        }

        boolean conflictingStaffRole =
                (role == UserRole.CLINICIAN
                        && userRoleList.contains(UserRole.CLINIC_REGISTRAR))
                        ||
                        (role == UserRole.CLINIC_REGISTRAR
                                && userRoleList.contains(UserRole.CLINICIAN));

        if (conflictingStaffRole) {
            throw new IncompatibleUserRoleException(
                    "A user cannot be both a clinician and a clinic registrar."
            );
        }

        if (!userRoleList.contains(role)) {
            userRoleList.add(role);
        }
    }

    public List<UserRole> getUserRoleList() {
        return List.copyOf(userRoleList);
    } //Ctrl + Shift + F 'yi unutma baya işlevsel bir şey.

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

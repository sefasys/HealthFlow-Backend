package com.healthflow.domain.model.user.staff;

import com.healthflow.domain.exception.InvalidDateException;
import com.healthflow.domain.exception.InvalidEmployeeIdException;
import com.healthflow.domain.exception.InvalidStaffException;
import com.healthflow.domain.exception.InvalidUserException;
import com.healthflow.domain.model.user.User;
import java.time.LocalDate;

public class Staff {
  private final String employeeId;
  private final LocalDate hireDate;
  private final EmploymentStatus employmentStatus;
  private final User user;

  public Staff(
      String employeeId, LocalDate hireDate, EmploymentStatus employmentStatus, User user) {
    if (employeeId == null | employeeId.isBlank()) {
      throw new InvalidEmployeeIdException("Employee id can not be null or blank.");
    }

    if (hireDate == null) {
      throw new InvalidDateException("Hire Date can not be null.");
    }

    if (employmentStatus == null) {
      throw new InvalidStaffException("Employment status information can not be null.");
    }

    if (user == null) {
      throw new InvalidUserException("User can not be null.");
    }

    this.employeeId = employeeId;
    this.hireDate = hireDate;
    this.employmentStatus = employmentStatus;
    this.user = user;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public LocalDate getHireDate() {
    return hireDate;
  }

  public EmploymentStatus getEmploymentStatus() {
    return employmentStatus;
  }

  public User getUser() {
    return user;
  }
}

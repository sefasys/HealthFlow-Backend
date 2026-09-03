package com.healthflow.domain.model.user.staff;

import com.healthflow.domain.model.user.User;
import java.time.LocalDate;

public class Staff {
  private final String employeeId;
  private final LocalDate hireDate;
  private final EmploymentStatus employmentStatus;
  private final User user;

  public Staff(
      String employeeId, LocalDate hireDate, EmploymentStatus employmentStatus, User user) {
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

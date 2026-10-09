package com.healthflow.domain.model.user.staff;

import com.healthflow.domain.exception.InvalidStaffException;

public class ClinicRegistrar {
  private final Staff staff;

  public ClinicRegistrar(Staff staff) {
    if (staff == null) throw new InvalidStaffException("Staff cannot be null.");
    this.staff = staff;
  }

  public Staff getStaff() { return staff; }
}

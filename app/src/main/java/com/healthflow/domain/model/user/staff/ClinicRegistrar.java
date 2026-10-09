package com.healthflow.domain.model.user.staff;

import com.healthflow.domain.exception.InvalidStaffException;

/** Registrar profile. Booking orchestration belongs to application services. */
public class ClinicRegistrar {
  private final Staff staff;

  public ClinicRegistrar(Staff staff) {
    if (staff == null) throw new InvalidStaffException("Staff cannot be null.");
    this.staff = staff;
  }

  public Staff getStaff() { return staff; }
}

package com.healthflow.domain.model.user.staff;

import com.healthflow.domain.exception.InvalidStaffException;

/** Clinician profile. Availability records are queried through a repository. */
public class Clinician {
  private final Staff staff;

  public Clinician(Staff staff) {
    if (staff == null) throw new InvalidStaffException("Staff cannot be null.");
    this.staff = staff;
  }

  public Staff getStaff() { return staff; }
}

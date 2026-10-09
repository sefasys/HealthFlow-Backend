package com.healthflow.domain.model.user.staff;

import com.healthflow.domain.exception.*;
import com.healthflow.domain.model.appointment.*;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.service.AppointmentScheduler;
import java.util.List;
import java.util.UUID;

public class ClinicRegistrar {
  private final Staff staff;

  public ClinicRegistrar(Staff staff) {
    if (staff == null) throw new InvalidStaffException("Staff can not be null.");
    this.staff = staff;

  }

    public Staff getStaff() {
        return staff;
    }
}

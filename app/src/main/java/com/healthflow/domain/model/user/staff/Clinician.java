package com.healthflow.domain.model.user.staff;

import com.healthflow.domain.model.appointment.Availability;
import com.healthflow.domain.model.clinicaldepartment.ClinicalDepartment;
import java.util.ArrayList;
import java.util.List;

public class Clinician {
  Staff staff;
  ClinicalDepartment department;
  List<Availability> availabilities;

  public Staff getStaff() {
    return staff;
  }

  public ClinicalDepartment getDepartment() {
    return department;
  }

  public List<Availability> getAvailabilities() {
    return availabilities;
  }

  public Clinician(Staff staff, ClinicalDepartment department) {

    if (staff == null) throw new IllegalArgumentException("Staff information can not be null");

    if (department == null)
      throw new IllegalArgumentException("Department information can not be null");

    this.staff = staff;
    this.department = department;
    availabilities = new ArrayList<>();
  }

  public void addAvailability(Availability availability) {
    if (availability == null) throw new IllegalArgumentException("Availability can not be null");

    if (availability.getClinician() != this) {
      throw new IllegalArgumentException("Clinicians are not matched!");
    }

    availabilities.add(availability);
  }

  public boolean removeAvailability(Availability availability) {
    if (availability == null) {
      throw new IllegalArgumentException("Availability can not be null");
    } else if (availability.getClinician() != this) {
      throw new IllegalArgumentException("Clinicians are not matched!");
    } else {
      return availabilities.remove(availability);
    }
  }

  public void updateAvailability(Availability oldAvailability, Availability newAvailability) {
    if (oldAvailability == null) throw new IllegalArgumentException("Availability can not be null");

    if (oldAvailability.getClinician() != this) {
      throw new IllegalArgumentException("Clinicians are not matched!");
    }
    if (newAvailability == null) throw new IllegalArgumentException("Availability can not be null");

    if (newAvailability.getClinician() != this) {
      throw new IllegalArgumentException("Clinicians are not matched!");
    }
    int indexOfOldAvailability = availabilities.indexOf(oldAvailability);
    if (indexOfOldAvailability == -1) {
      throw new IllegalArgumentException(
          "Old availability doesn't match with the availabilities list.");
    }

    for (Availability availability : availabilities) {
      if (!availability.equals(oldAvailability)
          && newAvailability.getDate().equals(availability.getDate())
          && newAvailability.getTimeRange().overlaps(availability.getTimeRange())) {
        throw new IllegalArgumentException(
            "New availability overlaps with an existing availability.");
      }
    }

    availabilities.set(indexOfOldAvailability, newAvailability);
  }
}

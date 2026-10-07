package com.healthflow.domain.model.user.staff;

import com.healthflow.domain.exception.InvalidAvailabilityException;
import com.healthflow.domain.exception.InvalidClinicianException;

import com.healthflow.domain.exception.InvalidStaffException;
import com.healthflow.domain.model.appointment.Availability;

import java.util.ArrayList;
import java.util.List;

public class Clinician {
  private final Staff staff;

  private final List<Availability> availabilities;

  public Clinician(Staff staff) {

    if (staff == null) throw new InvalidStaffException("Staff information can not be null");



    this.staff = staff;

    availabilities = new ArrayList<>();
  }

  public void addAvailability(Availability availability) {
    if (availability == null)
      throw new InvalidAvailabilityException("Availability can not be null");

    if (availability.getClinician() != this) {
      throw new InvalidClinicianException("Clinicians are not matched!");
    }

    availabilities.add(availability);
  }

  public boolean removeAvailability(Availability availability) {
    if (availability == null) {
      throw new InvalidAvailabilityException("Availability can not be null");
    } else if (availability.getClinician() != this) {
      throw new InvalidClinicianException(
          "Clinicians are not matched!"); // ? burası böyle mi olmalı yoksa yine availability mi?
    } else {
      return availabilities.remove(availability);
    }
  }

  public void updateAvailability(Availability oldAvailability, Availability newAvailability) {
    if (oldAvailability == null)
      throw new InvalidAvailabilityException("Availability can not be null");

    if (oldAvailability.getClinician() != this) {
      throw new InvalidAvailabilityException("Clinicians are not matched!");
    }
    if (newAvailability == null)
      throw new InvalidAvailabilityException("Availability can not be null");

    if (newAvailability.getClinician() != this) {
      throw new InvalidClinicianException("Clinicians are not matched!");
    }
    int indexOfOldAvailability = availabilities.indexOf(oldAvailability);
    if (indexOfOldAvailability == -1) {
      throw new InvalidAvailabilityException(
          "Old availability doesn't match with the availabilities list.");
    }

    for (Availability availability : availabilities) {
      if (!availability.equals(oldAvailability)
          && newAvailability.getDate().equals(availability.getDate())
          && newAvailability.getTimeRange().overlaps(availability.getTimeRange())) {
        throw new InvalidAvailabilityException(
            "New availability overlaps with an existing availability.");
      }
    }

    availabilities.set(indexOfOldAvailability, newAvailability);
  }

  public Staff getStaff() {
    return staff;
  }



  public List<Availability> getAvailabilities() {
    return availabilities;
  }
}

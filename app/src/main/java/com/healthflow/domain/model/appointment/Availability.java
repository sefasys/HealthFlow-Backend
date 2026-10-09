package com.healthflow.domain.model.appointment;

import com.healthflow.domain.exception.InvalidAvailabilityException;
import com.healthflow.domain.model.user.staff.Clinician;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

public class Availability {
  public static final Duration SLOT_DURATION = Duration.ofMinutes(15);
  private final UUID uniqueId;
  private final Clinician clinician;
  private final LocalDate date;
  private final TimeRange timeRange;

  public Availability(UUID uniqueId, Clinician clinician, LocalDate date, TimeRange timeRange) {
    if (uniqueId == null || clinician == null || date == null || timeRange == null) {
      throw new InvalidAvailabilityException("ID, clinician, date and time range are required.");
    }
    if (timeRange.start().getSecond() != 0 || timeRange.start().getNano() != 0
        || timeRange.end().getSecond() != 0 || timeRange.end().getNano() != 0
        || !timeRange.canBeSplitInto(SLOT_DURATION)) {
      throw new InvalidAvailabilityException("Availability must contain whole 15-minute slots with minute precision.");
    }
    this.uniqueId = uniqueId;
    this.clinician = clinician;
    this.date = date;
    this.timeRange = timeRange;
  }

  public boolean accepts(TimeRange requested) {
    return requested != null && timeRange.contains(requested)
        && requested.duration().equals(SLOT_DURATION)
        && Duration.between(timeRange.start(), requested.start()).toNanos()
            % SLOT_DURATION.toNanos() == 0;
  }

  public boolean overlaps(Availability other) {
    if (other == null) throw new InvalidAvailabilityException("Availability cannot be null.");
    return clinician.getStaff().getUser().getUniqueId()
        .equals(other.clinician.getStaff().getUser().getUniqueId())
        && date.equals(other.date) && timeRange.overlaps(other.timeRange);
  }

  public UUID getUniqueId() { return uniqueId; }
  public Clinician getClinician() { return clinician; }
  public LocalDate getDate() { return date; }
  public TimeRange getTimeRange() { return timeRange; }
}

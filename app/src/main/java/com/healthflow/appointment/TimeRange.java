package com.healthflow.appointment;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public record TimeRange(LocalTime start, LocalTime end) {

  public TimeRange {
    if (start == null || end == null) {
      throw new IllegalArgumentException("Start and end time cannot be null");
    }

    if (!start.isBefore(end)) {
      throw new IllegalArgumentException("Start time must be before end time");
    }
  }

  public boolean contains(LocalTime time) {
    return !time.isBefore(start) && time.isBefore(end);
  }

  public boolean contains(TimeRange other) {
    return !other.start().isBefore(start) && !other.end().isAfter(end);
  }

  public boolean overlaps(TimeRange other) {
    return start.isBefore(other.end()) && other.start().isBefore(end);
  }

  public Duration duration() {
    return Duration.between(start, end);
  }

  public boolean isAlignedTo(Duration step) {
    validatePositiveDuration(step);

    long minutes = step.toMinutes();

    return start.getMinute() % minutes == 0
        && end.getMinute() % minutes == 0
        && start.getSecond() == 0
        && end.getSecond() == 0;
  }

  public boolean canBeSplitInto(Duration slotDuration) {
    validatePositiveDuration(slotDuration);

    long rangeMinutes = duration().toMinutes();
    long slotMinutes = slotDuration.toMinutes();

    return rangeMinutes % slotMinutes == 0;
  }

  public List<TimeRange> split(Duration slotDuration) {
    validatePositiveDuration(slotDuration);

    if (!canBeSplitInto(slotDuration)) {
      throw new IllegalArgumentException(
          "Time range cannot be evenly split into given slot duration");
    }

    List<TimeRange> slots = new ArrayList<>();

    LocalTime current = start;

    while (current.isBefore(end)) {
      LocalTime slotEnd = current.plus(slotDuration);

      slots.add(new TimeRange(current, slotEnd));

      current = slotEnd;
    }

    return List.copyOf(slots);
  }

  private static void validatePositiveDuration(Duration duration) {
    if (duration == null || duration.isZero() || duration.isNegative()) {

      throw new IllegalArgumentException("Duration must be positive");
    }
  }
}

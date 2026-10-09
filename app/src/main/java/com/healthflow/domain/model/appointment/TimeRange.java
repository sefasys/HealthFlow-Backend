package com.healthflow.domain.model.appointment;

import com.healthflow.domain.exception.InvalidDurationException;
import com.healthflow.domain.exception.InvalidTimeRangeException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** Same-day half-open interval [start, end); midnight-crossing ranges are unsupported. */
public record TimeRange(LocalTime start, LocalTime end) {
  public TimeRange {
    if (start == null || end == null || !start.isBefore(end)) {
      throw new InvalidTimeRangeException("Start and end must be set and start must precede end.");
    }
  }

  public boolean contains(LocalTime time) {
    if (time == null) throw new InvalidTimeRangeException("Time cannot be null.");
    return !time.isBefore(start) && time.isBefore(end);
  }

  public boolean contains(TimeRange other) {
    requireRange(other);
    return !other.start.isBefore(start) && !other.end.isAfter(end);
  }

  public boolean overlaps(TimeRange other) {
    requireRange(other);
    return start.isBefore(other.end) && other.start.isBefore(end);
  }

  public Duration duration() { return Duration.between(start, end); }

  /** Alignment relative to midnight, without truncating seconds or hours. */
  public boolean isAlignedTo(Duration step) {
    validatePositiveDuration(step);
    if (step.compareTo(Duration.ofDays(1)) > 0) return false;
    long nanos = step.toNanos();
    return start.toNanoOfDay() % nanos == 0 && end.toNanoOfDay() % nanos == 0;
  }

  public boolean canBeSplitInto(Duration step) {
    validatePositiveDuration(step);
    if (step.compareTo(duration()) > 0) return false;
    return duration().toNanos() % step.toNanos() == 0;
  }

  public List<TimeRange> split(Duration step) {
    if (!canBeSplitInto(step)) {
      throw new InvalidTimeRangeException("Time range must be evenly divisible by slot duration.");
    }
    long count = duration().toNanos() / step.toNanos();
    // Avoid unbounded allocations for accidental nanosecond-sized slots.
    if (count > 86400) throw new InvalidDurationException("Too many slots in a single range.");
    List<TimeRange> ranges = new ArrayList<>();
    LocalTime cursor = start;
    for (long i = 0; i < count; i++) {
      LocalTime next = cursor.plus(step);
      ranges.add(new TimeRange(cursor, next));
      cursor = next;
    }
    return List.copyOf(ranges);
  }

  private static void validatePositiveDuration(Duration duration) {
    if (duration == null || duration.isZero() || duration.isNegative()) {
      throw new InvalidDurationException("Duration must be positive.");
    }
  }

  private static void requireRange(TimeRange range) {
    if (range == null) throw new InvalidTimeRangeException("Time range cannot be null.");
  }
}

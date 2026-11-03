package com.healthflow.entity.availability;


import com.healthflow.entity.user.staff.Clinician;
import java.time.Instant;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

/** A dated, same-day work interval; weekly recurrence is not implied. */
public class Availability {
  public static final Duration SLOT_DURATION = Duration.ofMinutes(15);
  private final UUID uniqueId;
  private final Clinician clinician;
  private final LocalDate date;
  private final TimeRange timeRange;
  private AvailabilityStatus status = AvailabilityStatus.PENDING_REVIEW;
  private UUID reviewedByUserId;
  private Instant reviewedAt;
  private String rejectionReason;


}

package com.healthflow.domain.model.appointment;

import com.healthflow.domain.exception.InvalidAvailabilityException;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.domain.model.user.staff.ClinicRegistrar;
import com.healthflow.domain.model.user.staff.EmploymentStatus;
import com.healthflow.domain.model.user.UserRole;
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

  /** Called by AvailabilityService after authorizing the authenticated registrar. */
  public void publish(ClinicRegistrar registrar, Instant reviewedAt) {
    requirePendingReview();
    validateReviewer(registrar, reviewedAt);
    if (clinician.getStaff().getEmploymentStatus() != EmploymentStatus.ACTIVE) {
      throw new InvalidAvailabilityException("Only an active clinician's availability can be published.");
    }
    this.reviewedByUserId = registrar.getStaff().getUser().getUniqueId();
    this.reviewedAt = reviewedAt;
    this.status = AvailabilityStatus.PUBLISHED;
  }

  public void reject(ClinicRegistrar registrar, Instant reviewedAt, String reason) {
    requirePendingReview();
    validateReviewer(registrar, reviewedAt);
    if (reason == null || reason.isBlank()) {
      throw new InvalidAvailabilityException("A rejection reason is required.");
    }
    this.reviewedByUserId = registrar.getStaff().getUser().getUniqueId();
    this.reviewedAt = reviewedAt;
    this.rejectionReason = reason.strip();
    this.status = AvailabilityStatus.REJECTED;
  }

  private void requirePendingReview() {
    if (status != AvailabilityStatus.PENDING_REVIEW) {
      throw new InvalidAvailabilityException("Only pending availability can be reviewed.");
    }
  }

  private static void validateReviewer(ClinicRegistrar registrar, Instant reviewedAt) {
    if (registrar == null || reviewedAt == null) {
      throw new InvalidAvailabilityException("Registrar and review time are required.");
    }
    if (registrar.getStaff().getEmploymentStatus() != EmploymentStatus.ACTIVE
        || !registrar.getStaff().getUser().getUserRoleList().contains(UserRole.CLINIC_REGISTRAR)) {
      throw new InvalidAvailabilityException("Reviewer must be an active clinic registrar.");
    }
  }

  /** Trusted persistence mapping only; never bind client input directly to this method. */
  public static Availability restore(UUID id, Clinician clinician, LocalDate date, TimeRange range,
      AvailabilityStatus status, UUID reviewedByUserId, Instant reviewedAt, String rejectionReason) {
    Availability result = new Availability(id, clinician, date, range);
    if (status == null) throw new InvalidAvailabilityException("Status is required.");
    if (status == AvailabilityStatus.PENDING_REVIEW) {
      if (reviewedByUserId != null || reviewedAt != null || rejectionReason != null) {
        throw new InvalidAvailabilityException("Pending availability cannot have review metadata.");
      }
    } else {
      if (reviewedByUserId == null || reviewedAt == null) {
        throw new InvalidAvailabilityException("Reviewed availability requires reviewer and time.");
      }
      if (status == AvailabilityStatus.REJECTED
          && (rejectionReason == null || rejectionReason.isBlank())) {
        throw new InvalidAvailabilityException("Rejected availability requires a reason.");
      }
      if (status == AvailabilityStatus.PUBLISHED && rejectionReason != null) {
        throw new InvalidAvailabilityException("Published availability cannot have a rejection reason.");
      }
    }
    result.status = status;
    result.reviewedByUserId = reviewedByUserId;
    result.reviewedAt = reviewedAt;
    result.rejectionReason = rejectionReason == null ? null : rejectionReason.strip();
    return result;
  }

  public boolean isPublished() { return status == AvailabilityStatus.PUBLISHED; }
  public AvailabilityStatus getStatus() { return status; }
  public UUID getReviewedByUserId() { return reviewedByUserId; }
  public Instant getReviewedAt() { return reviewedAt; }
  public String getRejectionReason() { return rejectionReason; }

  /** Geometric slot validity; publication and conflicts are separate checks. */
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

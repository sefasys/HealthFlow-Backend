package com.healthflow.infrastructure.persistence.entity;

import com.healthflow.domain.model.appointment.AvailabilityStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "availabilities", indexes = {
        @Index(name = "idx_availabilities_clinician_date", columnList = "clinician_id, work_date"),
        @Index(name = "idx_availabilities_status", columnList = "status")
}) //bu ne niye yaptık bunu?
public class AvailabilityEntity {

    @Id
    @Column(name = "unique_id")
    private UUID uniqueId;


    @ManyToOne(fetch = FetchType.LAZY, optional = false) //optional ne işe yarıyordu
    @JoinColumn(name = "clinician_id", nullable = false)
    private StaffEntity staff;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AvailabilityStatus status;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Version
    private Long version;


    public AvailabilityEntity(UUID uniqueId, String rejectionReason, Instant reviewedAt, UUID reviewedBy, AvailabilityStatus status, LocalTime endTime, LocalTime startTime, LocalDate workDate, StaffEntity staff) {
        this.uniqueId = uniqueId;
        this.rejectionReason = rejectionReason;
        this.reviewedAt = reviewedAt;
        this.reviewedBy = reviewedBy;
        this.status = status;
        this.endTime = endTime;
        this.startTime = startTime;
        this.workDate = workDate;
        this.staff = staff;
    }

    protected AvailabilityEntity(){}

    public UUID getUniqueId() {
        return uniqueId;
    }

    public StaffEntity getStaff() {
        return staff;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    public UUID getReviewedBy() {
        return reviewedBy;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void applyReview(AvailabilityStatus status, UUID reviewedBy, Instant reviewedAt, String rejectionReason){
        this.status = status;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
        this.rejectionReason = rejectionReason;
    }
}

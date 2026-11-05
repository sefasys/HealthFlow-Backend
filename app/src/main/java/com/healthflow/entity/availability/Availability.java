package com.healthflow.entity.availability;


import com.healthflow.entity.user.staff.Clinician;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "availabilities")
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "availability_id")
    private UUID availabilityId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Column(name = "clinician_id", nullable = false)
    private Clinician clinician;//ilişkilerde optional kullanıyoruz peki diğer fieldlarda neden kullanmıyoruz?

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_duration", nullable = false, length = 10)
    private SlotDuration slotDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AvailabilityStatus status;

    public Availability(Clinician clinician, LocalDateTime startTime, LocalDateTime endTime, SlotDuration slotDuration, AvailabilityStatus status) {
        this.clinician = clinician;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotDuration = slotDuration;
        this.status = AvailabilityStatus.ACTIVE;
    }

    protected Availability() {
    }

    public UUID getAvailabilityId() {
        return availabilityId;
    }

    public Clinician getClinician() {
        return clinician;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public SlotDuration getSlotDuration() {
        return slotDuration;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    public void cancel() {
        if (status == AvailabilityStatus.CANCELLED) {
            throw new IllegalStateException("Availability is already cancelled");
        }
        this.status = AvailabilityStatus.CANCELLED;
    }

    public boolean isActive() {
        return status == AvailabilityStatus.ACTIVE;
    }


}

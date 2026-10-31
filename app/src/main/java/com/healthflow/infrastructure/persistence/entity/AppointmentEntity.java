package com.healthflow.infrastructure.persistence.entity;

import com.healthflow.domain.model.appointment.AppointmentStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointments_patient_date", columnList = "patient_id, appointment_date"),
        @Index(name = "idx_appointments_clinician_date", columnList = "clinician_id, appointment_date")
})
public class AppointmentEntity {

    @Id
    @Column(name = "unique_id")
    private UUID uniqueId;

    @Column(name = "availability_id")
    private UUID availabilityId;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinician_id", nullable = false)
    private StaffEntity clinician;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AppointmentStatus status;

    @Version
    private Long version;


    public AppointmentEntity(UUID uniqueId, UUID availabilityId, PatientEntity patient, StaffEntity clinician, LocalDate date, LocalTime startTime, LocalTime endTime, AppointmentStatus status) {
        this.uniqueId = uniqueId;
        this.availabilityId = availabilityId;
        this.patient = patient;
        this.clinician = clinician;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public UUID getAvailabilityId() {
        return availabilityId;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public StaffEntity getClinician() {
        return clinician;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    protected AppointmentEntity(){}

    public void changeStatus(AppointmentStatus status){
        this.status = status;
    }

}

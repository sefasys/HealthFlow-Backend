package com.healthflow.entity.appointment;


import com.healthflow.entity.availability.Availability;
import com.healthflow.entity.user.patient.Patient;
import com.healthflow.entity.user.staff.ClinicRegistrar;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "appointments", uniqueConstraints = @UniqueConstraint(
        name = "uk_appointment_slot",
        columnNames = {"availability_id", "start_time"})) // Aslında bu unique anotasyonunun birden fazla label'ı birbirine şartlı şekilde bağlayıp unique olmasını sağlayan anotasyon.
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "appointment_id")
    private UUID appointmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Column(name = "availability_id", nullable = false)
    private Availability availability;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Column(name = "registrar_id", nullable = false)
    private ClinicRegistrar clinicRegistrar;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "patient_id")
    private Patient patient;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status;

    public Appointment(Availability availability, ClinicRegistrar clinicRegistrar, Patient patient, LocalDateTime startTime, LocalDateTime endTime, AppointmentStatus status) {
        this.availability = availability;
        this.clinicRegistrar = clinicRegistrar;
        this.patient = patient;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AppointmentStatus.AVAILABLE;
    }

    protected Appointment(){}

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public Availability getAvailability() {
        return availability;
    }

    public ClinicRegistrar getClinicRegistrar() {
        return clinicRegistrar;
    }

    public Patient getPatient() {
        return patient;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void book(Patient patient) {
        requireStatus(AppointmentStatus.AVAILABLE);
        this.patient = patient;
        this.status = AppointmentStatus.BOOKED;
    }

    public void release() {
        requireStatus(AppointmentStatus.BOOKED);
        this.patient = null;
        this.status = AppointmentStatus.AVAILABLE;
    }

    public void complete() {
        requireStatus(AppointmentStatus.BOOKED);
        this.status = AppointmentStatus.COMPLETED;
    }

    public void cancel() {
        requireStatus(AppointmentStatus.AVAILABLE, AppointmentStatus.BOOKED);
        this.status = AppointmentStatus.CANCELLED;
    }

    private void requireStatus(AppointmentStatus... allowed) {
        if (!List.of(allowed).contains(status)) {
            throw new IllegalStateException("Invalid status transition from " + status);
        }
    }



}

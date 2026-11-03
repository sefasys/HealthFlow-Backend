package com.healthflow.repository;

import com.healthflow.entity.appointment.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAppointmentRepository {
    Appointment save(Appointment appointment);

    Optional<Appointment> findById(UUID appointmentId);

    List<Appointment> findByPatientId(UUID patientUserId);

    List<Appointment> findByClinicianIdAndDate(
            UUID clinicianUserId,
            LocalDate date
    );

    List<Appointment> findByUserIdAndDate(
            UUID userId,
            LocalDate date
    );
}

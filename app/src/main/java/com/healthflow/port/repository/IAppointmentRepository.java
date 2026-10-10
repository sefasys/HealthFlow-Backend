package com.healthflow.port.repository;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.model.user.staff.Clinician;

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

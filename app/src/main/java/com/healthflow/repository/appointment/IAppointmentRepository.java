package com.healthflow.repository.appointment;

import com.healthflow.entity.appointment.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAppointmentRepository {

    List<Appointment> saveAll(List<Appointment> appointments);

    Optional<Appointment> findById(UUID id);

    boolean existsByAvailabilityId(UUID availabilityId);

    List<Appointment> findByAvailability(UUID availabilityId);

    boolean existsBookedOverlapForPatient(UUID patientId, LocalDateTime startTime, LocalDateTime endTime);
}
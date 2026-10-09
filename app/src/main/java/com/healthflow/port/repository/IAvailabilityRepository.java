package com.healthflow.port.repository;

import com.healthflow.domain.model.appointment.Availability;
import com.healthflow.domain.model.appointment.AvailabilityStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAvailabilityRepository {

    Availability save(Availability availability);

    Optional<Availability> findById(UUID availabilityId);

    List<Availability> findByClinicianId(UUID clinicianId);

    List<Availability> findByClinicianIdAndDate(UUID clinicianId, LocalDate date);

    List<Availability> findByStatus(AvailabilityStatus availabilityStatus);
}

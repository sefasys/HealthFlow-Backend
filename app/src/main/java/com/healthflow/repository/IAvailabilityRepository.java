package com.healthflow.repository;

import com.healthflow.entity.availability.Availability;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAvailabilityRepository {

    Availability save(Availability availability);

    List<Availability> getAvailabilities();

    Optional<Availability> findById(UUID availabilityId);

    List<Availability> findByClinicianId(UUID clinicianId);

    List<Availability> findByClinicianIdAndDate(UUID clinicianId, LocalDate date);

    List<Availability> findByStatus(AvailabilityStatus availabilityStatus);
}

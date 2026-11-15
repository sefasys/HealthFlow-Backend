package com.healthflow.repository.availability;

import com.healthflow.entity.availability.Availability;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAvailabilityRepository {

    Availability save(Availability availability);

    Optional<Availability> findById(UUID id);

    boolean existsById(UUID id);

    boolean existsActiveOverlapping(UUID clinicianId, LocalDateTime startTime, LocalDateTime endTime);

    List<Availability> findUpcomingActiveByClinician(UUID clinicianId, LocalDateTime now);
}
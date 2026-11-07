package com.healthflow.repository;


import com.healthflow.entity.availability.Availability;
import com.healthflow.entity.availability.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IAvailabilityRepository extends JpaRepository<Availability, UUID> {

    @Query("""
        select count(a) > 0 from Availability a
        where a.clinician.id = :clinicianId
          and a.status = :status
          and a.startTime < :endTime
          and a.endTime > :startTime
        """)
    boolean existsOverlapping(UUID clinicianId, LocalDateTime startTime,
                              LocalDateTime endTime, AvailabilityStatus status);

    List<Availability> findByClinicianIdAndStatusOrderByStartTime(
            UUID clinicianId, AvailabilityStatus status);

    List<Availability> findByClinicianIdAndStatusAndEndTimeAfterOrderByStartTime(
            UUID clinicianId, AvailabilityStatus status, LocalDateTime now);

}

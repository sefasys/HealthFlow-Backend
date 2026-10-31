package com.healthflow.infrastructure.persistence.repository;

import com.healthflow.domain.model.appointment.AvailabilityStatus;
import com.healthflow.infrastructure.persistence.entity.AvailabilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAvailabilityRepository extends JpaRepository<AvailabilityEntity, UUID> {
    @Query("""
           select a from AvailabilityEntity a
           join fetch a.staff c
           join fetch c.user
           """)
    List<AvailabilityEntity> findAllWithClinician();

    @Query("""
           select a from AvailabilityEntity a
           join fetch a.staff c
           join fetch c.user
           where a.uniqueId = :id
           """)
    Optional<AvailabilityEntity> findWithClinicianById(@Param("id") UUID id);

    @Query("""
           select a from AvailabilityEntity a
           join fetch a.staff c
           join fetch c.user
           where c.userId = :clinicianId
           """)
    List<AvailabilityEntity> findByClinicianId(@Param("clinicianId") UUID clinicianId);

    @Query("""
           select a from AvailabilityEntity a
           join fetch a.staff c
           join fetch c.user
           where c.userId = :clinicianId and a.workDate = :date
           """)
    List<AvailabilityEntity> findByClinicianIdAndDate(@Param("clinicianId") UUID clinicianId,
                                                      @Param("date") LocalDate date);

    @Query("""
           select a from AvailabilityEntity a
           join fetch a.staff c
           join fetch c.user
           where a.status = :status
           """)
    List<AvailabilityEntity> findByStatus(@Param("status") AvailabilityStatus status);

}

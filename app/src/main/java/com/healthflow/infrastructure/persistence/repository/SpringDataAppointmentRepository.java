package com.healthflow.infrastructure.persistence.repository;

import com.healthflow.infrastructure.persistence.entity.AppointmentEntity;
import com.healthflow.port.repository.IAppointmentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {

    @Query("""
           select a from AppointmentEntity a
           join fetch a.patient p
           join fetch p.user
           join fetch a.clinician c
           join fetch c.user
           where a.uniqueId = :id
           """)
    Optional<AppointmentEntity> findWithParticipantsById(@Param("id") UUID id);

    @Query("""
           select a from AppointmentEntity a
           join fetch a.patient p
           join fetch p.user
           join fetch a.clinician c
           join fetch c.user
           where p.uniqueId = :patientId
           """)
    List<AppointmentEntity> findByPatientId(@Param("patientId") UUID patientId);

    @Query("""
           select a from AppointmentEntity a
           join fetch a.patient p
           join fetch p.user
           join fetch a.clinician c
           join fetch c.user
           where c.userId = :clinicianId and a.date = :date
           """)
    List<AppointmentEntity> findByClinicianIdAndDate(@Param("clinicianId") UUID clinicianId,
                                                     @Param("date") LocalDate date);

    @Query("""
           select a from AppointmentEntity a
           join fetch a.patient p
           join fetch p.user
           join fetch a.clinician c
           join fetch c.user
           where a.date = :date
             and (p.uniqueId = :userId or c.userId = :userId)
           """)
    List<AppointmentEntity> findByUserIdAndDate(@Param("userId") UUID userId,
                                                @Param("date") LocalDate date);
}

package com.healthflow.repository;


import com.healthflow.entity.appointment.Appointment;
import com.healthflow.entity.appointment.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IAppointmentRepository extends JpaRepository<Appointment, UUID> {

    boolean existsByAvailabilityId(UUID availabilityId);

    List<Appointment> findByAvailabilityIdOrderByStartTime(UUID availabilityId);

    @Query("""
    select count(a) > 0 from Appointment a
    where a.patient.id = :patientId
      and a.status = :status
      and a.startTime < :endTime
      and a.endTime > :startTime
    """)
    boolean existsPatientOverlap(UUID patientId, LocalDateTime startTime,
                                 LocalDateTime endTime, AppointmentStatus status); // Çakışma kontrolünü yapıyor bu metot.


}

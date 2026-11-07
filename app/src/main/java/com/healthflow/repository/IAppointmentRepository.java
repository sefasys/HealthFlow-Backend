package com.healthflow.repository;


import com.healthflow.entity.appointment.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IAppointmentRepository extends JpaRepository<Appointment, UUID> {

    boolean existsByAvailabilityId(UUID availabilityId);

    List<Appointment> findByAvailabilityIdOrderByStartTime(UUID availabilityId);


}

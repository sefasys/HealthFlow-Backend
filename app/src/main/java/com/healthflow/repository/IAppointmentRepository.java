package com.healthflow.repository;


import com.healthflow.entity.appointment.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IAppointmentRepository extends JpaRepository<Appointment, UUID> {



}

package com.healthflow.application.usecase.appointment;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.port.repository.AppointmentRepository;
import java.util.Optional;
import java.util.UUID;

public class FindAppointmentByUniqueIdUseCase {
  private final AppointmentRepository appointmentRepository;

  public FindAppointmentByUniqueIdUseCase(AppointmentRepository appointmentRepository) {
    this.appointmentRepository = appointmentRepository;
  }

  public Optional<Appointment> execute(UUID uniqueId) {
    return appointmentRepository.findByUniqueId(uniqueId);
  }
}

package com.healthflow.application.usecase.appointment;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.port.repository.IAppointmentRepository;
import java.util.Optional;
import java.util.UUID;

public class FindAppointmentByUniqueIdUseCase {
  private final IAppointmentRepository iAppointmentRepository;

  public FindAppointmentByUniqueIdUseCase(IAppointmentRepository iAppointmentRepository) {
    this.iAppointmentRepository = iAppointmentRepository;
  }

  public Optional<Appointment> execute(UUID uniqueId) {
    return iAppointmentRepository.findByUniqueId(uniqueId);
  }
}

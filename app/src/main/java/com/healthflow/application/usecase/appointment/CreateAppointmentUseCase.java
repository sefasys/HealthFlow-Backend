package com.healthflow.application.usecase.appointment;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.port.repository.IAppointmentRepository;

public class CreateAppointmentUseCase {
  private final IAppointmentRepository iAppointmentRepository;

  public CreateAppointmentUseCase(IAppointmentRepository iAppointmentRepository) {
    this.iAppointmentRepository = iAppointmentRepository;
  }

  public Appointment execute(Appointment appointment) {
    if (iAppointmentRepository.findByUniqueId(appointment.getUniqueId()).isPresent()) {
      throw new IllegalArgumentException("Appointment already exists");
    }

    iAppointmentRepository.addAppointment(appointment);

    return appointment;
  }
}

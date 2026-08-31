package com.healthflow.application.usecase.appointment;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.port.repository.AppointmentRepository;
import java.util.List;

public class GetAppointmentsUseCase {
  private final AppointmentRepository appointmentRepository;

  public GetAppointmentsUseCase(AppointmentRepository appointmentRepository) {
    this.appointmentRepository = appointmentRepository;
  }

  public List<Appointment> execute() {
    return appointmentRepository.getAppointments();
  }
}

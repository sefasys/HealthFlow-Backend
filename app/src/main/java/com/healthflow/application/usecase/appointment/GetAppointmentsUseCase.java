package com.healthflow.application.usecase.appointment;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.port.repository.IAppointmentRepository;
import java.util.List;

public class GetAppointmentsUseCase {
  private final IAppointmentRepository iAppointmentRepository;

  public GetAppointmentsUseCase(IAppointmentRepository iAppointmentRepository) {
    this.iAppointmentRepository = iAppointmentRepository;
  }

  public List<Appointment> execute() {
    return iAppointmentRepository.getAppointments();
  }
}

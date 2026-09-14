package com.healthflow.application.usecase.appointment;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.port.repository.IAppointmentRepository;
import java.util.List;

public class FindAppointmentByClinicianUseCase {
  private final IAppointmentRepository iAppointmentRepository;

  public FindAppointmentByClinicianUseCase(IAppointmentRepository iAppointmentRepository) {
    this.iAppointmentRepository = iAppointmentRepository;
  }

  public List<Appointment> execute(Clinician clinician) {
    return iAppointmentRepository.findByClinician(clinician);
  }
}

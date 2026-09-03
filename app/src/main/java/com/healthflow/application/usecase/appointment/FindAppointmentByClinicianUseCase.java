package com.healthflow.application.usecase.appointment;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.port.repository.AppointmentRepository;
import java.util.List;

public class FindAppointmentByClinicianUseCase {
  private final AppointmentRepository appointmentRepository;

  public FindAppointmentByClinicianUseCase(AppointmentRepository appointmentRepository) {
    this.appointmentRepository = appointmentRepository;
  }

  public List<Appointment> execute(Clinician clinician) {
    return appointmentRepository.findByClinician(clinician);
  }
}

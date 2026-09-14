package com.healthflow.application.usecase.appointment;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IAppointmentRepository;
import java.util.List;

public class FindAppointmentByPatientUseCase {
  private final IAppointmentRepository iAppointmentRepository;

  public FindAppointmentByPatientUseCase(IAppointmentRepository iAppointmentRepository) {
    this.iAppointmentRepository = iAppointmentRepository;
  }

  public List<Appointment> execute(Patient patient) {
    return iAppointmentRepository.findByPatient(patient);
  }
}

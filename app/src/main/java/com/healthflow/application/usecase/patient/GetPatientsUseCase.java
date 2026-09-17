package com.healthflow.application.usecase.patient;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import java.util.List;

public class GetPatientsUseCase {

  private final IPatientRepository patientRepository;

  public GetPatientsUseCase(IPatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  public List<Patient> execute() {
    return patientRepository.getPatients();
  }
}

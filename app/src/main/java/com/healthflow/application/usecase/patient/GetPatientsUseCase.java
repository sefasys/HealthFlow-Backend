package com.healthflow.application.usecase.patient;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import java.util.List;

public class GetPatientsUseCase {

  private final IPatientRepository iPatientRepository;

  public GetPatientsUseCase(IPatientRepository iPatientRepository) {
    this.iPatientRepository = iPatientRepository;
  }

  public List<Patient> execute() {
    return iPatientRepository.getPatients();
  }
}

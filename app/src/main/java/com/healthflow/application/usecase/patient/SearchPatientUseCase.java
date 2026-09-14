package com.healthflow.application.usecase.patient;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import java.util.List;

public class SearchPatientUseCase {
  private final IPatientRepository iPatientRepository;

  public SearchPatientUseCase(IPatientRepository iPatientRepository) {
    this.iPatientRepository = iPatientRepository;
  }

  public List<Patient> execute(String query) {
    return iPatientRepository.search(query);
  }
}

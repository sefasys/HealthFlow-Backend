package com.healthflow.application.usecase.patient;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.PatientRepository;
import java.util.List;

public class SearchPatientUseCase {
  private final PatientRepository patientRepository;

  public SearchPatientUseCase(PatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  public List<Patient> execute(String query) {
    return patientRepository.search(query);
  }
}

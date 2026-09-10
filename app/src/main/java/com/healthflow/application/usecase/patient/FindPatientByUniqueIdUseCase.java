package com.healthflow.application.usecase.patient;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.PatientRepository;
import java.util.Optional;
import java.util.UUID;

public class FindPatientByUniqueIdUseCase {
  private final PatientRepository patientRepository;

  public FindPatientByUniqueIdUseCase(PatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  public Optional<Patient> execute(UUID id) {
    return patientRepository.findByUniqueId(id);
  }
}

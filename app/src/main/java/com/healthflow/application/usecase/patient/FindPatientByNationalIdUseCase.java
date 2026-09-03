package com.healthflow.application.usecase.patient;

import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.PatientRepository;
import java.util.Optional;

public class FindPatientByNationalIdUseCase {
  private final PatientRepository patientRepository;

  public FindPatientByNationalIdUseCase(PatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  public Optional<Patient> execute(NationalId nationalId) {
    return patientRepository.findByNationalId(nationalId);
  }
}

package com.healthflow.application.usecase.patient;

import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.domain.exception.InvalidNationalIdException;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;

public class FindPatientByNationalIdUseCase {
  private final IPatientRepository patientRepository;

  public FindPatientByNationalIdUseCase(IPatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  public Patient execute(NationalId nationalId) {

    if (nationalId == null) {
      throw new InvalidNationalIdException("National ID cannot be null.");
    }
    return patientRepository
        .findByNationalId(nationalId)
        .orElseThrow(
            () -> new PatientNotFoundException("Patient not found with this national id."));
  }
}

package com.healthflow.application.usecase.patient;

import com.healthflow.domain.exception.InvalidNationalIdException;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import java.util.Optional;

public class FindPatientByNationalIdUseCase {
  private final IPatientRepository iPatientRepository;

  public FindPatientByNationalIdUseCase(IPatientRepository iPatientRepository) {
    this.iPatientRepository = iPatientRepository;
  }

  public Optional<Patient> execute(NationalId nationalId) {

    if (nationalId == null) {
      throw new InvalidNationalIdException("National ID cannot be null.");
    }
    return iPatientRepository.findByNationalId(nationalId);
  }
}

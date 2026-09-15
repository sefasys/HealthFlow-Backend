package com.healthflow.application.usecase.patient;


import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import java.util.Optional;
import java.util.UUID;

public class FindPatientByUniqueIdUseCase {
  private final IPatientRepository iPatientRepository;

  public FindPatientByUniqueIdUseCase(IPatientRepository iPatientRepository) {
    this.iPatientRepository = iPatientRepository;
  }

  public Optional<Patient> execute(UUID id) {

    if (id == null) {
      throw new InvalidUniqueIdException("Unique ID cannot be null.");
    }

    return iPatientRepository.findByUniqueId(id);
  }
}

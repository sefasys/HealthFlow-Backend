package com.healthflow.application.usecase.patient;


import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import java.util.UUID;

public class FindPatientByUniqueIdUseCase {
  private final IPatientRepository patientRepository;

  public FindPatientByUniqueIdUseCase(IPatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  public Patient execute(UUID id) {

    if (id == null) {
      throw new InvalidUniqueIdException("Unique ID cannot be null.");
    }

    return patientRepository.findByUniqueId(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with unique id: " + id));
  }
}

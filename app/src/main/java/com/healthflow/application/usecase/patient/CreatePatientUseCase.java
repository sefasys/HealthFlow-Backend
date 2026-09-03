package com.healthflow.application.usecase.patient;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.PatientRepository;

public class CreatePatientUseCase {

  private final PatientRepository patientRepository;

  public CreatePatientUseCase(PatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  public Patient execute(Patient patient) {

    if (patientRepository.findByNationalId(patient.getUser().getNationalId()).isPresent()) {
      throw new IllegalArgumentException("Patient already exists");
    }

    patientRepository.addPatient(patient);

    return patient;
  }
}

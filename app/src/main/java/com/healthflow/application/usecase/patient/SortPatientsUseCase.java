package com.healthflow.application.usecase.patient;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import com.healthflow.port.repository.PatientSortType;
import java.util.List;

public class SortPatientsUseCase {
  private final IPatientRepository patientRepository;

  public SortPatientsUseCase(IPatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  public List<Patient> execute(PatientSortType sortType) {

    return patientRepository.sort(sortType);
  }
}

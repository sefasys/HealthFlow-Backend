package com.healthflow.application.usecase.patient;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import com.healthflow.port.repository.PatientSortType;
import java.util.List;

public class SortPatientsUseCase {
  private final IPatientRepository iPatientRepository;

  public SortPatientsUseCase(IPatientRepository iPatientRepository) {
    this.iPatientRepository = iPatientRepository;
  }

  public List<Patient> execute(PatientSortType sortType) {
    return iPatientRepository.sort(sortType);
  }
}

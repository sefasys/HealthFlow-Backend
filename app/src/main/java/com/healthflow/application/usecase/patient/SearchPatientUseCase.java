package com.healthflow.application.usecase.patient;

import com.healthflow.application.exception.InvalidSearchQueryException;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import java.util.List;

public class SearchPatientUseCase {
  private final IPatientRepository patientRepository;

  public SearchPatientUseCase(IPatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  public List<Patient> execute(String query) {

      if (query == null || query.isBlank()) {
          throw new InvalidSearchQueryException(
                  "Search query cannot be null or blank."
          );
      }

      return patientRepository.search(query);
  }
}

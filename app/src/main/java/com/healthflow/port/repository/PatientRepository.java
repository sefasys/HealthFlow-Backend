package com.healthflow.port.repository;

import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.patient.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientRepository {

  List<Patient> getPatients();

  void addPatient(Patient patient);

  List<Patient> sort(PatientSortType sortType);

  Optional<Patient> findByUniqueId(Long uniqueId);

  Optional<Patient> findByNationalId(NationalId nationalId);

  List<Patient> search(String query);
}

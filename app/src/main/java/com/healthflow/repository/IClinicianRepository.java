package com.healthflow.repository;

import com.healthflow.entity.user.staff.Clinician;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IClinicianRepository {

    List<Clinician> getClinicians();

    void addClinician(Clinician clinician);

    Optional<Clinician> findByUniqueId(UUID uniqueId);

    Optional<Clinician> findByNationalId(NationalId nationalId);

    List<Clinician> search(String query);

    void update(Clinician clinician);
}

package com.healthflow.repository;

import com.healthflow.entity.user.staff.ClinicRegistrar;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IClinicRegistrarRepository {
    List<ClinicRegistrar> getClinicRegistrars();

    void addClinicRegistrar(ClinicRegistrar clinicRegistrar);

    Optional<ClinicRegistrar> findByUniqueId(UUID uniqueId);

    Optional<ClinicRegistrar> findByNationalId(NationalId nationalId);

    List<ClinicRegistrar> search(String query);

    void update(ClinicRegistrar clinicRegistrar);
}

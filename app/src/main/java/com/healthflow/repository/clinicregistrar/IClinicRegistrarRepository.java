package com.healthflow.repository.clinicregistrar;

import com.healthflow.entity.staff.ClinicRegistrar;

import java.util.Optional;
import java.util.UUID;

public interface IClinicRegistrarRepository {

    ClinicRegistrar save(ClinicRegistrar registrar);

    Optional<ClinicRegistrar> findDetailedById(UUID id);
}
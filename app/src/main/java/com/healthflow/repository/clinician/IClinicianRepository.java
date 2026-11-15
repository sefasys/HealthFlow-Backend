package com.healthflow.repository.clinician;

import com.healthflow.entity.staff.Clinician;
import com.healthflow.entity.staff.Specialty;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IClinicianRepository {

    Clinician save(Clinician clinician);

    boolean existsById(UUID id);

    boolean existsByLicenseNumber(String licenseNumber);

    Optional<Clinician> findDetailedById(UUID id);

    List<Clinician> findActiveBySpecialty(Specialty specialty);

    Optional<Clinician> findByLicenseNumber(String licenseNumber);

    Optional<Clinician> findByEmployeeNumber(String employeeNumber);
}
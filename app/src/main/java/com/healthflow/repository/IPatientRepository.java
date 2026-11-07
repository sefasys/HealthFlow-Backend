package com.healthflow.repository;


import com.healthflow.entity.user.patient.Patient;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IPatientRepository extends JpaRepository<Patient, UUID> {

    @EntityGraph(attributePaths = {"user", "allergies"})
    Optional<Patient> findWithDetailsById(UUID id); // N+1'i entity graph anotasyonu ile engelledik.

    @EntityGraph(attributePaths = {"user", "allergies"})
    Optional<Patient> findByUserNationalId(String nationalId);

}

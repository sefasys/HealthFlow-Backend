package com.healthflow.repository.patient;


import com.healthflow.entity.user.Gender;
import com.healthflow.entity.patient.BloodType;
import com.healthflow.entity.patient.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface IPatientRepository{

    Patient save(Patient patient);

    Optional<Patient> findById(UUID id);

    Optional<Patient> findDetailedById(UUID id);

    Optional<Patient> findByNationalId(String nationalId);

    Page<Patient> search(String query, Gender gender, BloodType bloodType, Pageable pageable);
}


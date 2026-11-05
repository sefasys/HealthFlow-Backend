package com.healthflow.repository;


import com.healthflow.entity.user.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IPatientRepository extends JpaRepository<Patient, UUID> {


}

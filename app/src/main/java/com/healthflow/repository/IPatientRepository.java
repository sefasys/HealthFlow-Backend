package com.healthflow.repository;


import com.healthflow.entity.user.Gender;
import com.healthflow.entity.user.patient.BloodType;
import com.healthflow.entity.user.patient.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface IPatientRepository extends JpaRepository<Patient, UUID> {

    @EntityGraph(attributePaths = {"user", "allergies"})
    Optional<Patient> findWithDetailsById(UUID id); // N+1'i entity graph anotasyonu ile engelledik.

    @EntityGraph(attributePaths = {"user", "allergies"})
    Optional<Patient> findByUserNationalId(String nationalId);

    @EntityGraph(attributePaths = "user")
    @Query("""
    select p from Patient p
    join p.user u
    where (lower(u.name) like lower(concat('%', :query, '%'))
           or lower(u.surname) like lower(concat('%', :query, '%'))
           or u.nationalId like concat(:query, '%'))
      and (:gender is null or u.gender = :gender)
      and (:bloodType is null or p.bloodType = :bloodType)
    """)
    Page<Patient> search(String query, Gender gender, BloodType bloodType, Pageable pageable);

}

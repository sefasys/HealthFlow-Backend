package com.healthflow.infrastructure.persistence.repository;

import com.healthflow.infrastructure.persistence.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataPatientRepository extends JpaRepository<PatientEntity, UUID> {

    @Query("""
           select p from PatientEntity p
           join fetch p.user u
           where u.nationalId = :nationalId
           """)
    Optional<PatientEntity> findByNationalId(@Param("nationalId") String nationalId);

    @Query("""
           select p from PatientEntity p
           join fetch p.user u
           where lower(u.name)    like lower(concat('%', :q, '%'))
              or lower(u.surname) like lower(concat('%', :q, '%'))
              or u.nationalId     like concat('%', :q, '%')
           """)
    List<PatientEntity> search(@Param("q") String query);
}

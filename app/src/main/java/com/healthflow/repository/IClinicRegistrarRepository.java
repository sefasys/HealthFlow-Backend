package com.healthflow.repository;


import com.healthflow.entity.user.staff.ClinicRegistrar;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IClinicRegistrarRepository extends JpaRepository<ClinicRegistrar, UUID> {

    @EntityGraph(attributePaths = {"staff", "staff.user"})
    Optional<ClinicRegistrar> findWithDetailsById(UUID id);
}

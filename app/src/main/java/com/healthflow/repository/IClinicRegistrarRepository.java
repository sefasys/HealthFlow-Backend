package com.healthflow.repository;


import com.healthflow.entity.user.staff.ClinicRegistrar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IClinicRegistrarRepository extends JpaRepository<ClinicRegistrar, UUID> {

}

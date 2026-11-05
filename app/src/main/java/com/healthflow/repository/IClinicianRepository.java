package com.healthflow.repository;


import com.healthflow.entity.user.staff.Clinician;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IClinicianRepository extends JpaRepository<Clinician, UUID> {


}

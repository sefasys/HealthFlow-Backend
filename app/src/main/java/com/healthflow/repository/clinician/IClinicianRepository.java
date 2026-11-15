package com.healthflow.repository.clinician;


import com.healthflow.entity.staff.Clinician;
import com.healthflow.entity.staff.Specialty;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IClinicianRepository extends JpaRepository<Clinician, UUID> {
    boolean existsByLicenseNumber(String licenseNumber);

    @EntityGraph(attributePaths = {"staff", "staff.user"})
    Optional<Clinician> findWithDetailsById(UUID id);

    @Query("""
    select c from Clinician c
    join fetch c.staff s
    join fetch s.user
    where c.specialty = :specialty
      and s.employmentStatus = com.healthflow.entity.staff.EmploymentStatus.ACTIVE
    """)
    List<Clinician> findActiveBySpecialty(Specialty specialty);

    @EntityGraph(attributePaths = {"staff", "staff.user"})
    Optional<Clinician> findByLicenseNumber(String licenseNumber);

    @EntityGraph(attributePaths = {"staff", "staff.user"})
    Optional<Clinician> findByStaffEmployeeNumber(String employeeNumber);
}

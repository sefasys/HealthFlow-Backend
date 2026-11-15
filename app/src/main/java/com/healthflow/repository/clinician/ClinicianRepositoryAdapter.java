package com.healthflow.repository.clinician;

import com.healthflow.entity.staff.Clinician;
import com.healthflow.entity.staff.EmploymentStatus;
import com.healthflow.entity.staff.Specialty;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClinicianRepositoryAdapter implements IClinicianRepository {

    private static final String SELECT_WITH_DETAILS = """
            select c from Clinician c
            join fetch c.staff s
            join fetch s.user u
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Clinician save(Clinician clinician) {
        if (clinician.getId() == null) {
            entityManager.persist(clinician);
            return clinician;
        }
        return entityManager.merge(clinician);
    }

    @Override
    public boolean existsById(UUID id) {
        return entityManager.createQuery(
                        "select count(c) from Clinician c where c.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    @Override
    public boolean existsByLicenseNumber(String licenseNumber) {
        return entityManager.createQuery(
                        "select count(c) from Clinician c where c.licenseNumber = :licenseNumber", Long.class)
                .setParameter("licenseNumber", licenseNumber)
                .getSingleResult() > 0;
    }

    @Override
    public Optional<Clinician> findDetailedById(UUID id) {
        return entityManager.createQuery(SELECT_WITH_DETAILS + "where c.id = :id", Clinician.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Clinician> findActiveBySpecialty(Specialty specialty) {
        return entityManager.createQuery(SELECT_WITH_DETAILS + """
                        where c.specialty = :specialty
                          and s.employmentStatus = :status
                        order by u.surname
                        """, Clinician.class)
                .setParameter("specialty", specialty)
                .setParameter("status", EmploymentStatus.ACTIVE)
                .getResultList();
    }

    @Override
    public Optional<Clinician> findByLicenseNumber(String licenseNumber) {
        return entityManager.createQuery(SELECT_WITH_DETAILS + "where c.licenseNumber = :licenseNumber", Clinician.class)
                .setParameter("licenseNumber", licenseNumber)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<Clinician> findByEmployeeNumber(String employeeNumber) {
        return entityManager.createQuery(SELECT_WITH_DETAILS + "where s.employeeNumber = :employeeNumber", Clinician.class)
                .setParameter("employeeNumber", employeeNumber)
                .getResultStream()
                .findFirst();
    }
}
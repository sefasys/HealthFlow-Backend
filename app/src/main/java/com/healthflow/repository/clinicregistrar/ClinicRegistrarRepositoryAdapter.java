package com.healthflow.repository.clinicregistrar;

import com.healthflow.entity.staff.ClinicRegistrar;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ClinicRegistrarRepositoryAdapter implements IClinicRegistrarRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ClinicRegistrar save(ClinicRegistrar registrar) {
        if (registrar.getId() == null) {
            entityManager.persist(registrar);
            return registrar;
        }
        return entityManager.merge(registrar);
    }

    @Override
    public Optional<ClinicRegistrar> findDetailedById(UUID id) {
        return entityManager.createQuery("""
                        select r from ClinicRegistrar r
                        join fetch r.staff s
                        join fetch s.user
                        where r.id = :id
                        """, ClinicRegistrar.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }
}
package com.healthflow.repository.availability;

import com.healthflow.entity.availability.Availability;
import com.healthflow.entity.availability.AvailabilityStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AvailabilityRepositoryAdapter implements IAvailabilityRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Availability save(Availability availability) {
        if (availability.getId() == null) {
            entityManager.persist(availability);
            return availability;
        }
        return entityManager.merge(availability);
    }

    @Override
    public Optional<Availability> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(Availability.class, id));
    }

    @Override
    public boolean existsById(UUID id) {
        return entityManager.createQuery(
                        "select count(a) from Availability a where a.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    @Override
    public boolean existsActiveOverlapping(UUID clinicianId, LocalDateTime startTime, LocalDateTime endTime) {
        return entityManager.createQuery("""
                        select count(a) from Availability a
                        where a.clinician.id = :clinicianId
                          and a.status = :status
                          and a.startTime < :endTime
                          and a.endTime > :startTime
                        """, Long.class)
                .setParameter("clinicianId", clinicianId)
                .setParameter("status", AvailabilityStatus.ACTIVE)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .getSingleResult() > 0;
    }

    @Override
    public List<Availability> findUpcomingActiveByClinician(UUID clinicianId, LocalDateTime now) {
        return entityManager.createQuery("""
                        select a from Availability a
                        where a.clinician.id = :clinicianId
                          and a.status = :status
                          and a.endTime > :now
                        order by a.startTime
                        """, Availability.class)
                .setParameter("clinicianId", clinicianId)
                .setParameter("status", AvailabilityStatus.ACTIVE)
                .setParameter("now", now)
                .getResultList();
    }
}
package com.healthflow.repository.appointment;

import com.healthflow.entity.appointment.Appointment;
import com.healthflow.entity.appointment.AppointmentStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AppointmentRepositoryAdapter implements IAppointmentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Appointment> saveAll(List<Appointment> appointments) {
        appointments.forEach(entityManager::persist);
        return appointments;
    }

    @Override
    public Optional<Appointment> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(Appointment.class, id));
    }

    @Override
    public boolean existsByAvailabilityId(UUID availabilityId) {
        return entityManager.createQuery(
                        "select count(a) from Appointment a where a.availability.id = :availabilityId", Long.class)
                .setParameter("availabilityId", availabilityId)
                .getSingleResult() > 0;
    }

    @Override
    public List<Appointment> findByAvailability(UUID availabilityId) {
        return entityManager.createQuery("""
                        select a from Appointment a
                        join fetch a.availability av
                        where av.id = :availabilityId
                        order by a.startTime
                        """, Appointment.class)
                .setParameter("availabilityId", availabilityId)
                .getResultList();
    }

    @Override
    public boolean existsBookedOverlapForPatient(UUID patientId, LocalDateTime startTime, LocalDateTime endTime) {
        return entityManager.createQuery("""
                        select count(a) from Appointment a
                        where a.patient.id = :patientId
                          and a.status = :status
                          and a.startTime < :endTime
                          and a.endTime > :startTime
                        """, Long.class)
                .setParameter("patientId", patientId)
                .setParameter("status", AppointmentStatus.BOOKED)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .getSingleResult() > 0;
    }
}
package com.healthflow.repository.staff;

import com.healthflow.entity.staff.Staff;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class StaffRepositoryAdapter implements IStaffRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Staff save(Staff staff) {
        if (staff.getId() == null) {
            entityManager.persist(staff);
            return staff;
        }
        return entityManager.merge(staff);
    }

    @Override
    public Optional<Staff> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(Staff.class, id));
    }

    @Override
    public boolean existsByEmployeeNumber(String employeeNumber) {
        return entityManager.createQuery(
                        "select count(s) from Staff s where s.employeeNumber = :employeeNumber", Long.class)
                .setParameter("employeeNumber", employeeNumber)
                .getSingleResult() > 0;
    }
}
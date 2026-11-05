package com.healthflow.repository;

import com.healthflow.entity.user.staff.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IStaffRepository extends JpaRepository<Staff, UUID> {
    boolean existsByEmployeeNumber(String employeeNumber);
}

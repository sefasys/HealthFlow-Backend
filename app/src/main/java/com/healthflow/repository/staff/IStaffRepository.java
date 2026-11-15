package com.healthflow.repository.staff;

import com.healthflow.entity.staff.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IStaffRepository extends JpaRepository<Staff, UUID> {
    boolean existsByEmployeeNumber(String employeeNumber);
}

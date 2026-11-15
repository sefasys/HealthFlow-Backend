package com.healthflow.repository.staff;

import com.healthflow.entity.staff.Staff;

import java.util.Optional;
import java.util.UUID;

public interface IStaffRepository {

    Staff save(Staff staff);

    Optional<Staff> findById(UUID id);

    boolean existsByEmployeeNumber(String employeeNumber);
}
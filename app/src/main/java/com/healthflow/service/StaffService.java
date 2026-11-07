package com.healthflow.service;

import com.healthflow.entity.user.User;
import com.healthflow.entity.user.UserRole;
import com.healthflow.entity.user.staff.Staff;
import com.healthflow.exception.StaffAlreadyExistsException;
import com.healthflow.exception.StaffNotFoundException;
import com.healthflow.repository.IStaffRepository;
import com.healthflow.repository.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class StaffService {

    private final UserService userService;
    private final IUserRepository userRepository;
    private final IStaffRepository staffRepository;

    public StaffService(UserService userService, IUserRepository userRepository, IStaffRepository staffRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
    }

    @Transactional
    public Staff createStaff(User user, UserRole role, String employeeNumber, LocalDate hireDate) {
        userService.ensureUnique(user.getNationalId(), user.getEmail());
        if (staffRepository.existsByEmployeeNumber(employeeNumber)) {
            throw new StaffAlreadyExistsException("Employee number already in use");
        }

        user.addRole(role);
        userRepository.save(user);

        Staff staff = new Staff(user, hireDate, employeeNumber);
        return staffRepository.save(staff);
    }


    @Transactional
    public void startLeave(UUID staffId)      { findStaff(staffId).startLeave(); }

    @Transactional
    public void returnFromLeave(UUID staffId) { findStaff(staffId).returnFromLeave(); }

    @Transactional
    public void suspend(UUID staffId)         { findStaff(staffId).suspend(); }

    @Transactional
    public void reinstate(UUID staffId)       { findStaff(staffId).reinstate(); }

    @Transactional
    public void terminate(UUID staffId)       { findStaff(staffId).terminate(); }

    private Staff findStaff(UUID id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new StaffNotFoundException("Staff is not found with the ID: " + id));
    }
}

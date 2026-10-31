package com.healthflow.infrastructure.persistence.entity;

import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.staff.EmploymentStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "staff")
public class StaffEntity {


    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "employee_id", nullable = false, unique = true)
    private UUID employeeId;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false, length = 16)
    private EmploymentStatus employmentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_type", nullable = false, length = 25)
    private StaffType staffType;

    @Version
    private Long version;

    public StaffEntity(StaffType staffType, EmploymentStatus employmentStatus, LocalDate hireDate, UUID employeeId, UserEntity user) {
        this.staffType = staffType;
        this.employmentStatus = employmentStatus;
        this.hireDate = hireDate;
        this.employeeId = employeeId;
        this.user = user;
    }

    protected StaffEntity(){}

    public UserEntity getUser() {
        return user;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public StaffType getStaffType() {
        return staffType;
    }

    public UUID getUserId() {
        return userId;
    }

    public void changeEmploymentStatus(EmploymentStatus employmentStatus){
        this.employmentStatus = employmentStatus;
    }

}

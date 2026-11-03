package com.healthflow.entity.user.staff;


import com.healthflow.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "staff")
public class Staff {

    @Id
    private UUID id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false, length = 16)
    private EmploymentStatus employmentStatus;

    public Staff(User user, LocalDate hireDate) {
        this.user = user;
        this.hireDate = hireDate;
        this.employmentStatus = EmploymentStatus.ACTIVE;
    }

    protected Staff() {}

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public void startLeave() {
        requireStatus(EmploymentStatus.ACTIVE);
        this.employmentStatus = EmploymentStatus.ON_LEAVE;
    }

    public void returnFromLeave() {
        requireStatus(EmploymentStatus.ON_LEAVE);
        this.employmentStatus = EmploymentStatus.ACTIVE;
    }

    public void suspend() {
        requireStatus(EmploymentStatus.ACTIVE, EmploymentStatus.ON_LEAVE);
        this.employmentStatus = EmploymentStatus.SUSPENDED;
    }

    public void reinstate() {
        requireStatus(EmploymentStatus.SUSPENDED);
        this.employmentStatus = EmploymentStatus.ACTIVE;
    }

    public void terminate() {
        requireStatus(EmploymentStatus.ACTIVE, EmploymentStatus.ON_LEAVE, EmploymentStatus.SUSPENDED);
        this.employmentStatus = EmploymentStatus.TERMINATED;
    }

    public boolean isActive() {
        return employmentStatus == EmploymentStatus.ACTIVE;
    }

    private void requireStatus(EmploymentStatus... allowed) {
        if (!List.of(allowed).contains(employmentStatus)) {
            throw new IllegalStateException(
                    "Cannot change status from " + employmentStatus);
        }
    }
}

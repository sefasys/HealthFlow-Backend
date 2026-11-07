package com.healthflow.entity.user.staff;


import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "clinic_registrars")
public class ClinicRegistrar {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "staff_id")
    private Staff staff;


    public ClinicRegistrar(Staff staff) {
        this.staff = Objects.requireNonNull(staff);
    }

    protected ClinicRegistrar() {}

    public UUID getId() {
        return id;
    }

    public Staff getStaff() {
        return staff;
    }

}

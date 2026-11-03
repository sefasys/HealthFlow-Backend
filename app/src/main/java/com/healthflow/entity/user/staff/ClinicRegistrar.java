package com.healthflow.entity.user.staff;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "clinic_registrar")
public class ClinicRegistrar {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "staff_id")
    private Staff staff;


    public ClinicRegistrar(Staff staff, String licenseNumber) {
        this.staff = staff;
    }

    protected ClinicRegistrar() {}

    public UUID getId() {
        return id;
    }

    public Staff getStaff() {
        return staff;
    }

}

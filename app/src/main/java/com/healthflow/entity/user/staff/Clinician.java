package com.healthflow.entity.user.staff;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "clinician")
public class Clinician {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @Enumerated(EnumType.STRING)
    @Column(name = "specialty", length = 30)
    private Specialty specialty;

    @Column(name = "license_number", nullable = false, unique = true, length = 30)
    private String licenseNumber;

    public Clinician(Staff staff, Specialty specialty, String licenseNumber) {
        this.staff = staff;
        this.specialty = specialty;
        this.licenseNumber = licenseNumber;
    }

    protected Clinician() {}

    public UUID getId() {
        return id;
    }

    public Staff getStaff() {
        return staff;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }
}

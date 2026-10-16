package com.healthflow.infrastructure.persistence.entity;


import com.healthflow.domain.model.user.patient.BloodType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "patients")
public class PatientEntity {

    @Id
    @Column(name = "unique_id")
    private UUID uniqueId;

    @OneToOne(optional = false) //optional ne anlama geliyor?
    @MapsId // Bu ne anlama geliyor
    @JoinColumn(name = "unique_id") // bu ne anlama geliyor?
    private UserEntity user;

    @Enumerated(EnumType.STRING) // bunu bil.
    @Column(name = "blood_type", nullable = false)
    private BloodType bloodType;

    public PatientEntity(UserEntity user,
                         UUID uniqueId,
                         BloodType bloodType){
        this.user = user;
        this.uniqueId = uniqueId;
        this.bloodType = bloodType;

    } // Bizim mapper’da mevcut verilerle entity oluşturabilmemiz için.


    protected PatientEntity(){ // Hibernate'in objeyi oluşturabilmesi için.

    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public UserEntity getUser() {
        return user;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    // PatientEntity
    public void changeBloodType(BloodType bloodType) { this.bloodType = bloodType; }
}

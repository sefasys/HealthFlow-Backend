package com.healthflow.entity.user.patient;


import com.healthflow.entity.user.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true) // örnekte lazy fetch var ama niye bilmiyorum.
    private User user;

    @ElementCollection
    @CollectionTable(name = "patient_allergies", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "allergy", nullable = false, length = 100)
    private Set<String> allergies = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type", length = 16)
    private BloodType bloodType;

    public Patient(User user, Set<String> allergies) {
        this.user = user;
        this.allergies = new HashSet<>(allergies);
    }
    protected Patient(){}

    public User getUser() {
        return user;
    }

    public Set<String> getAllergies() {
        return allergies;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void addAllergy(String allergy) { allergies.add(allergy); }
    public void removeAllergy(String allergy) { allergies.remove(allergy); }

}

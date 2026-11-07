package com.healthflow.entity.user.patient;


import com.healthflow.entity.user.User;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    @CollectionTable(name = "patient_allergies", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "allergy", nullable = false, length = 100)
    private Set<String> allergies = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type", length = 16)
    private BloodType bloodType;

    public Patient(User user) {
        this.user = user;
    }
    protected Patient(){}

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Set<String> getAllergies() {
        return Collections.unmodifiableSet(allergies);
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void updateBloodType(BloodType bloodType) {
        this.bloodType = Objects.requireNonNull(bloodType);
    }

    public void addAllergy(String allergy) { allergies.add(allergy); }
    public void replaceAllergies(Set<String> newAllergies) {
        allergies.clear();
        allergies.addAll(newAllergies);
    }
    public void removeAllergy(String allergy) { allergies.remove(allergy);}

}

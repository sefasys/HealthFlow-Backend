package com.healthflow.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @NotBlank(message = "National ID cannot be empty")
    @Pattern(regexp = "\\d{10}[02468]", message = "National ID must be 11 digits and end with an even digit")
    @Column(name = "national_id", nullable = false, unique = true, length = 11)
    private String nationalId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String surname;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, unique = true)
    // @Email bu anotasyonu dto'da kullanmak daha mantıklıymış.
    private String email;

    @Column(length = 40)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    private  Set<UserRole> userRoleSet = new HashSet<>();

    public User(String nationalId, String name, String surname, LocalDate birthDate, String email, String phoneNumber, Set<UserRole> userRoleSet) {
        this.nationalId = nationalId;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userRoleSet = new HashSet<>(userRoleSet);
    }

    protected User(){}

    public UUID getUserId() {
        return userId;
    }

    public String getNationalId() {
        return nationalId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Set<UserRole> getUserRoleSet() {
        return userRoleSet;
    }
}

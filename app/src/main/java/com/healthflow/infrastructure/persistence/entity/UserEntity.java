package com.healthflow.infrastructure.persistence.entity;

import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.UserRole;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users") // table ismi user olamaz.
public class UserEntity {

    @Id
    @Column(name = "unique_id")
    private UUID uniqueId;

    @Column(name = "national_id")
    private String nationalId; //bura bak

    @Column
    private String name;

    @Column
    private String surname;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @ElementCollection
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<UserRole> roles = new HashSet<>(); //Bu kısım N1 normalizayonunda sorunlu olmaz mı?
    /* Bu kısım çok önemli. Java Collection'daki her veriyi ayrı bir satır olarak saklıyoruz. @ElementCollection ile bunu sağladık.
     CollectionTable ile yeni bir table oluşturduk. Onun içerisinde bu listedeki verileri saklıyor olacağız.

      birden fazla değer taşıyor ama Java nesnesinin yapısı ile veritabanının tablo yapısı birebir aynı olmak zorunda değil.
      @ElementCollection ve @CollectionTable, Hibernate’e bu koleksiyonu ayrı tablonun satırlarına dönüştürmesini söylüyor.
     Bir de önceki örneğimi tamamlayayım: Set yalnızca Java tarafında tekrarı engeller. Veritabanında da aynı kullanıcı–rol
     çiftinin tekrarını engellemek için (user_id, role) üzerinde birleşik UNIQUE kısıtı tanımlayabiliriz.

      */

    // burada patient entity mapped by tarzı bir işleme gerek duymadık çünkü user entity tarafının şu anda patient table tarafıyla bir işi yok.

    public UserEntity(
            UUID uniqueId,
            String nationalId,
            String name,
            String surname,
            LocalDate birthDate,
            String email,
            String phoneNumber,
            Set<UserRole> roles) {

        this.uniqueId = uniqueId;
        this.nationalId = nationalId;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roles = new HashSet<>(roles);
    } //  Bizim mapper’da mevcut verilerle entity oluşturabilmemiz için.

    protected UserEntity(){} // Hibernate'in objeyi oluşturabilmesi için


    public UUID getUniqueId() {
        return uniqueId;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }



}

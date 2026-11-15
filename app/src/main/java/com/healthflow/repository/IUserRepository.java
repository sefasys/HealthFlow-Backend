package com.healthflow.repository;


import com.healthflow.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {
    boolean existsByNationalId(String nationalId);
    boolean existsByEmail(String email); //Bu metotları userservice classında kullanıyor olacağız.
    boolean existsByEmailAndUserIdNot(String email, UUID userId);

}
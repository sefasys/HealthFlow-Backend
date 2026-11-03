package com.healthflow.repository;

import com.healthflow.entity.user.NationalId;
import com.healthflow.entity.user.User;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository {

    User save(User user);

    Optional<User> findByUniqueId(UUID uniqueId);

    Optional<User> findByNationalId(NationalId nationalId);
}
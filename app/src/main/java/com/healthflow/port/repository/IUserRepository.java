package com.healthflow.port.repository;

import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository {

    User save(User user);

    Optional<User> findByUniqueId(UUID uniqueId);

    Optional<User> findByNationalId(NationalId nationalId);
}
package com.healthflow.infrastructure.repository;

import com.healthflow.application.exception.UserAlreadyExistsException;
import com.healthflow.domain.exception.InvalidNationalIdException;
import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.domain.exception.InvalidUserException;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.port.repository.IUserRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryUserRepository implements IUserRepository {

    private final Map<UUID, User> userMap = new HashMap<>();

    @Override
    public synchronized User save(User user) {
        if (user == null) {
            throw new InvalidUserException("User cannot be null.");
        }

        boolean nationalIdUsedByAnotherUser = userMap.values().stream()
                .anyMatch(existing ->
                        existing.getNationalId().equals(user.getNationalId())
                                && !existing.getUniqueId().equals(user.getUniqueId())
                );

        if (nationalIdUsedByAnotherUser) {
            throw new UserAlreadyExistsException(
                    "Another user already has this national ID."
            );
        }

        User existingUser = userMap.get(user.getUniqueId());

        if (existingUser != null
                && !existingUser.getNationalId().equals(user.getNationalId())) {
            throw new InvalidUserException(
                    "An existing user's national ID cannot be changed."
            );
        }

        userMap.put(user.getUniqueId(), user);
        return user;
    }

    @Override
    public synchronized Optional<User> findByUniqueId(UUID uniqueId) {
        if (uniqueId == null) {
            throw new InvalidUniqueIdException(
                    "User ID cannot be null."
            );
        }

        return Optional.ofNullable(userMap.get(uniqueId));
    }

    @Override
    public synchronized Optional<User> findByNationalId(NationalId nationalId) {
        if (nationalId == null) {
            throw new InvalidNationalIdException(
                    "National ID cannot be null."
            );
        }

        return userMap.values().stream()
                .filter(user -> user.getNationalId().equals(nationalId))
                .findFirst();
    }
}
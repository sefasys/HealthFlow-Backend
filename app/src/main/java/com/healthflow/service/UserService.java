package com.healthflow.service;


import com.healthflow.dto.user.UpdateContactInfoRequest;
import com.healthflow.entity.user.User;
import com.healthflow.exception.UserAlreadyExistsException;
import com.healthflow.exception.UserNotFoundException;
import com.healthflow.repository.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class UserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void ensureUnique(String nationalId, String email) {
        if (userRepository.existsByNationalId(nationalId)) {
            throw new UserAlreadyExistsException("A user with this national ID already exists");
        }
        if (userRepository.existsByEmail(email.toLowerCase())) {
            throw new UserAlreadyExistsException("A user with this email already exists");
        }
    }

    @Transactional
    public void updateContactInfo(UUID userId, UpdateContactInfoRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with : " + userId));

        String email = request.email().toLowerCase();
        if (userRepository.existsByEmailAndUserIdNot(email, userId)) {
            throw new UserAlreadyExistsException("A user with this email already exists");
        }

        user.updateContactInfo(email, request.phoneNumber());
    }
}


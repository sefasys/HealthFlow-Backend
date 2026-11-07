package com.healthflow.service;


import com.healthflow.exception.UserAlreadyExistsException;
import com.healthflow.repository.IUserRepository;
import org.springframework.stereotype.Service;


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
}


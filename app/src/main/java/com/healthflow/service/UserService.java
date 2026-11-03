package com.healthflow.service;

import com.healthflow.entity.factory.UserFactory;
import com.healthflow.entity.user.NationalId;
import com.healthflow.entity.user.User;
import com.healthflow.entity.user.UserRole;
import com.healthflow.repository.IUserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {

    private final IUserRepository userRepository;
    private final UserFactory userFactory;

    public UserService(
            IUserRepository userRepository,
            UserFactory userFactory
    ) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
    }

}
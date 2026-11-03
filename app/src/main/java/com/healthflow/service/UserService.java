package com.healthflow.service;

import com.healthflow.repository.IUserRepository;
import org.springframework.stereotype.Service;

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
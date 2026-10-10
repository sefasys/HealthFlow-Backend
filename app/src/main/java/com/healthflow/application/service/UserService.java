package com.healthflow.application.service;

import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.port.repository.IUserRepository;
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

    public User resolveUser(
            NationalId nationalId,
            String name,
            String surname,
            LocalDate birthDate,
            String email,
            String phoneNumber,
            UserRole initialRole
    ) {
        return userRepository.findByNationalId(nationalId)
                .orElseGet(() ->
                        userFactory.createUser(
                                nationalId,
                                name,
                                surname,
                                birthDate,
                                email,
                                phoneNumber,
                                initialRole
                        )
                );
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
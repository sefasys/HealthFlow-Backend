package com.healthflow.domain.factory;

import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@Component
public class UserFactory {

  public User createUser(
      NationalId nationalId,
      String name,
      String surname,
      LocalDate birthDate,
      String email,
      String phoneNumber,
      UserRole userRole) {

    User user =
        new User(
            UUID.randomUUID(),
            nationalId,
            name,
            surname,
            birthDate,
            email,
            phoneNumber,
            new ArrayList<>());
    user.addRole(userRole);
    return user;
  }
}

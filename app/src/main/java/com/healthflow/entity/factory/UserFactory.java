package com.healthflow.entity.factory;

import com.healthflow.entity.user.NationalId;
import com.healthflow.entity.user.User;
import com.healthflow.entity.user.UserRole;
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

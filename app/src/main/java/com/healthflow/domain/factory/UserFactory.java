package com.healthflow.domain.factory;

import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class UserFactory {

    public User createUser(
            String nationalId,
            String name,
            String surname,
            LocalDate birthDate,
            String email,
            String phoneNumber,
            UserRole userRole
    ) {
        NationalId nationalIdObject = new NationalId(nationalId);
        User user = new User(UUID.randomUUID(), nationalIdObject, name, surname, birthDate, email, phoneNumber, new ArrayList<>());
        user.setUserRole(userRole);
        return user;
    }




}

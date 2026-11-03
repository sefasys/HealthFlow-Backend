package com.healthflow.entity.user;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class User {

  private final UUID uniqueId;
  private final NationalId nationalId;
  private final String name;
  private final String surname;
  private final LocalDate birthDate;
  private String email;
  private String phoneNumber;
  private final List<UserRole> userRoleList;


}

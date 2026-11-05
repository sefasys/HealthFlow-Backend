package com.healthflow.dto.patient;

import com.healthflow.entity.user.Gender;
import com.healthflow.entity.user.patient.BloodType;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record PatientResponse(
                              UUID id,
                              String name,
                              String surname,
                              LocalDate birthDate,
                              String email,
                              String phoneNumber,
                              Gender gender,
                              BloodType bloodType,
                              Set<String> allergies
) {}

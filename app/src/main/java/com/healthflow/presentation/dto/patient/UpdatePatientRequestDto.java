package com.healthflow.presentation.dto.patient;

import com.healthflow.domain.model.user.patient.BloodType;

public record UpdatePatientRequestDto(String email, String phoneNumber, BloodType bloodType) {}

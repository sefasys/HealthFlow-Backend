package com.healthflow.application.dto.patient;

import com.healthflow.domain.model.user.patient.BloodType;

public record UpdatePatientRequestDto(String email, String phoneNumber, BloodType bloodType) {}
//Update için yeni response yazılabilir.
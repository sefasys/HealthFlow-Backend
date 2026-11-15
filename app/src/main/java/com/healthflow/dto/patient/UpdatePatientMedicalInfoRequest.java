package com.healthflow.dto.patient;

import com.healthflow.entity.patient.BloodType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdatePatientMedicalInfoRequest(
        BloodType bloodType,
        Set<@NotBlank @Size(max = 100) String> allergies
)  {}

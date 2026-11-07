package com.healthflow.dto.patient;

import com.healthflow.entity.user.patient.BloodType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdatePatientMedicalInfoRequest(
        BloodType bloodType,
        Set<@NotBlank @Size(max = 100) String> allergies
)  {}

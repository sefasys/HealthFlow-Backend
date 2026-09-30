package com.healthflow.presentation.mapper;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.presentation.dto.patient.PatientResponseDto;

public final class PatientMapper {

    private PatientMapper(){}

    public static PatientResponseDto toResponseDto(Patient patient){
        PatientResponseDto responseDto = new PatientResponseDto(
                patient.getUser().getUniqueId(),
                patient.getUser().getName(),
                patient.getUser().getSurname(),
                patient.getUser().getBirthDate()
        );

        return responseDto;
    }

}

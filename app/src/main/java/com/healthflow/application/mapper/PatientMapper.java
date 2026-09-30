package com.healthflow.application.mapper;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.presentation.dto.patient.PatientResponseDto;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    private PatientMapper(){}

    public PatientResponseDto toResponseDto(Patient patient){
        PatientResponseDto responseDto = new PatientResponseDto(
                patient.getUser().getUniqueId(),
                patient.getUser().getName(),
                patient.getUser().getSurname(),
                patient.getUser().getBirthDate()
        );

        return responseDto;
    }

}

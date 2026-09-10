package com.healthflow.presentation.controller;


import com.healthflow.application.usecase.patient.CreatePatientUseCase;
import com.healthflow.presentation.dto.patient.CreatePatientRequestDto;
import com.healthflow.presentation.dto.patient.PatientResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final CreatePatientUseCase createPatientUseCase;

    public PatientController(CreatePatientUseCase createPatientUseCase){
        this.createPatientUseCase = createPatientUseCase;
    }

    @PostMapping
    public PatientResponseDto createPatient(@RequestBody CreatePatientRequestDto requestDto){

    }

}

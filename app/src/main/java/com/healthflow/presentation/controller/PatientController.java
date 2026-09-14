package com.healthflow.presentation.controller;


import com.healthflow.application.usecase.patient.CreatePatientUseCase;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.presentation.dto.patient.CreatePatientRequestDto;
import com.healthflow.presentation.dto.patient.PatientResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final CreatePatientUseCase createPatientUseCase;

    public PatientController(CreatePatientUseCase createPatientUseCase){
        this.createPatientUseCase = createPatientUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientResponseDto createPatient(@Valid @RequestBody CreatePatientRequestDto requestDto){
        NationalId nationalId = new NationalId(requestDto.nationalId());
        Patient patient = createPatientUseCase.execute(
                nationalId,
                requestDto.name(),
                requestDto.surname(),
                requestDto.birthDate(),
                requestDto.email(),
                requestDto.phoneNumber()
        );
        User user = patient.getUser();
        PatientResponseDto responseDto = new PatientResponseDto(
                user.getUniqueID(),
                user.getName(),
                user.getSurname(),
                user.getBirthDate()
        );
        return responseDto;
    }

}

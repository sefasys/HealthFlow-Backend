package com.healthflow.presentation.controller;

import com.healthflow.application.dto.clinician.*;
import com.healthflow.application.service.ClinicianService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinician")
public class ClinicianController {

    private final ClinicianService clinicianService;

    public ClinicianController(ClinicianService clinicianService){
        this.clinicianService = clinicianService;
    }

    public ClinicianResponseDto createClinician(CreateClinicianRequestDto requestDto){
        return clinicianService.createClinician(requestDto);
    }

    public List<ClinicianResponseDto> getClinicians(){
        return clinicianService.getClinicians();
    }

    public ClinicianResponseDto findClinicianByUniqueId(UUID uniqueId){
        return clinicianService.findClinicianByUniqueId(uniqueId);
    }

    public ClinicianResponseDto findClinicianByNationalId(FindClinicianByNationalIdRequestDto requestDto){
        return clinicianService.findClinicianByNationalId(requestDto);
    }

    public UpdateClinicianResponseDto updateClinician(UUID uniqueId, UpdateClinicianRequestDto requestDto){
        return clinicianService.updateClinician(uniqueId, requestDto);
    }

    public List<ClinicianResponseDto> searchClinician(String query){
        return clinicianService.searchClinician(query);
    }
}

package com.healthflow.presentation.controller;

import com.healthflow.application.dto.clinician.*;
import com.healthflow.application.service.ClinicianService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinician")
public class ClinicianController {

    private final ClinicianService clinicianService;

    public ClinicianController(ClinicianService clinicianService){
        this.clinicianService = clinicianService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicianResponseDto createClinician(@Valid @RequestBody CreateClinicianRequestDto requestDto){
        return clinicianService.createClinician(requestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClinicianResponseDto> getClinicians(){
        return clinicianService.getClinicians();
    }

    @GetMapping("/find-by-unique-id/{uniqueId}")
    @ResponseStatus(HttpStatus.OK)
    public ClinicianResponseDto findClinicianByUniqueId(@Valid @PathVariable UUID uniqueId){
        return clinicianService.findClinicianByUniqueId(uniqueId);
    }

    @PostMapping("/find-by-national-id")
    @ResponseStatus(HttpStatus.OK)
    public ClinicianResponseDto findClinicianByNationalId(@Valid @RequestBody FindClinicianByNationalIdRequestDto requestDto){
        return clinicianService.findClinicianByNationalId(requestDto);
    }

    @PatchMapping("/update/{uniqueId}")
    @ResponseStatus(HttpStatus.OK)
    public UpdateClinicianResponseDto updateClinician(@PathVariable UUID uniqueId, @Valid @RequestBody UpdateClinicianRequestDto requestDto){
        return clinicianService.updateClinician(uniqueId, requestDto);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ClinicianResponseDto> searchClinician(@RequestParam String query){
        return clinicianService.searchClinician(query);
    }
}

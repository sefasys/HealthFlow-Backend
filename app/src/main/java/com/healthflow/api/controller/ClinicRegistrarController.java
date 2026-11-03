package com.healthflow.api.controller;

import com.healthflow.service.ClinicRegistrarService;
import com.healthflow.dto.clinicregistrar.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinicregistrar")
public class ClinicRegistrarController {

    private final ClinicRegistrarService clinicRegistrarService;

    public ClinicRegistrarController(ClinicRegistrarService clinicRegistrarService){
        this.clinicRegistrarService = clinicRegistrarService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicRegistrarResponseDto createClinicRegistrar(@Valid @RequestBody CreateClinicRegistrarRequestDto requestDto){
        return clinicRegistrarService.createClinicRegistrar(requestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClinicRegistrarResponseDto> getClinicRegistrars(){
        return clinicRegistrarService.getClinicRegistrars();
    }

    @GetMapping("/find-by-unique-id/{uniqueId}")
    @ResponseStatus(HttpStatus.OK)
    public ClinicRegistrarResponseDto findClinicRegistrarByUniqueId(@PathVariable UUID uniqueId){
        return clinicRegistrarService.findClinicRegistrarByUniqueId(uniqueId);
    }

    @PostMapping("/find-by-national-id")
    @ResponseStatus(HttpStatus.OK)
    public ClinicRegistrarResponseDto findClinicRegistrarByNationalId(@Valid @RequestBody FindClinicRegistrarByNationalIdRequestDto requestDto){
        return clinicRegistrarService.findClinicRegistrarByNationalId(requestDto);
    }

    @PatchMapping("/update/{uniqueId}")
    @ResponseStatus(HttpStatus.OK)
    public UpdateClinicRegistrarResponseDto updateClinicRegistrar(@PathVariable UUID uniqueId, @Valid @RequestBody UpdateClinicRegistrarRequestDto requestDto){
        return clinicRegistrarService.updateClinicRegistrar(uniqueId, requestDto);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ClinicRegistrarResponseDto> searchClinicRegistrar(@RequestParam String query){
        return clinicRegistrarService.searchClinicRegistrar(query);
    }
}


package com.healthflow.api.controller;

import com.healthflow.service.ClinicRegistrarService;
import com.healthflow.dto.clinicregistrar.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinic-registrars")
public class ClinicRegistrarController {

    private final ClinicRegistrarService clinicRegistrarService;

    public ClinicRegistrarController(ClinicRegistrarService clinicRegistrarService) {
        this.clinicRegistrarService = clinicRegistrarService;
    }

    @PostMapping
    public ResponseEntity<ClinicRegistrarResponse> register(
            @Valid @RequestBody ClinicRegistrarRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clinicRegistrarService.register(request));
    }

    @GetMapping("/{id}")
    public ClinicRegistrarResponse getById(@PathVariable UUID id) {
        return clinicRegistrarService.getById(id);
    }

}


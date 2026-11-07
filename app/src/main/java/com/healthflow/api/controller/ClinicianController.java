package com.healthflow.api.controller;

import com.healthflow.entity.user.staff.Specialty;
import com.healthflow.service.ClinicianService;
import com.healthflow.dto.clinician.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinician")
public class ClinicianController {

    private ClinicianService clinicianService;

    @PostMapping("/api/clinicians")
    public ResponseEntity<ClinicianResponseDto> register(
            @Valid @RequestBody ClinicianRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clinicianService.register(request));
    }

    @GetMapping("/{id}")
    public ClinicianResponseDto getById(@PathVariable UUID id) {
        return clinicianService.getById(id);
    }

    @GetMapping
    public List<ClinicianResponseDto> getBySpecialty(@RequestParam Specialty specialty) {
        return clinicianService.getActiveBySpecialty(specialty);
    }

    @GetMapping(params = "licenseNumber")
    public ClinicianResponseDto getByLicenseNumber(@RequestParam String licenseNumber) {
        return clinicianService.getByLicenseNumber(licenseNumber);
    }

    @GetMapping(params = "employeeNumber")
    public ClinicianResponseDto getByEmployeeNumber(@RequestParam String employeeNumber) {
        return clinicianService.getByEmployeeNumber(employeeNumber);
    }

}

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
    public ResponseEntity<ClinicianResponse> register(
            @Valid @RequestBody ClinicianRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clinicianService.register(request));
    }

    @GetMapping("/{id}")
    public ClinicianResponse getById(@PathVariable UUID id) {
        return clinicianService.getById(id);
    }

    @GetMapping
    public List<ClinicianResponse> getBySpecialty(@RequestParam Specialty specialty) {
        return clinicianService.getActiveBySpecialty(specialty);
    }

    @GetMapping(params = "licenseNumber")
    public ClinicianResponse getByLicenseNumber(@RequestParam String licenseNumber) {
        return clinicianService.getByLicenseNumber(licenseNumber);
    }

    @GetMapping(params = "employeeNumber")
    public ClinicianResponse getByEmployeeNumber(@RequestParam String employeeNumber) {
        return clinicianService.getByEmployeeNumber(employeeNumber);
    }

}

package com.healthflow.api.controller;

import com.healthflow.service.PatientService;
import com.healthflow.dto.patient.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

  private final PatientService patientService;


  public PatientController(PatientService patientService) {
    this.patientService = patientService;
  }

    @PostMapping
    public ResponseEntity<PatientResponse> register(
            @Valid @RequestBody PatientRegistrationRequest request) {
        PatientResponse response = patientService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public PatientResponse getById(@PathVariable UUID id) {
        return patientService.getById(id);
    }

    @PatchMapping("/{id}/medical-info")
    public PatientResponse updateMedicalInfo(@PathVariable UUID id,
                                             @Valid @RequestBody UpdatePatientMedicalInfoRequest request) {
        return patientService.updateMedicalInfo(id, request);
    }

    @GetMapping(params = "nationalId")
    public PatientResponse getByNationalId(@RequestParam String nationalId) {
        return patientService.getByNationalId(nationalId);
    }
}

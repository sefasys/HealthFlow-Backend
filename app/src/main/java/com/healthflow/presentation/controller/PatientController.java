package com.healthflow.presentation.controller;

import com.healthflow.application.service.PatientService;
import com.healthflow.presentation.dto.patient.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

  private final PatientService patientService;


  public PatientController(PatientService patientService) {
    this.patientService = patientService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PatientResponseDto createPatient(@Valid @RequestBody CreatePatientRequestDto requestDto) {
    return patientService.createPatient(requestDto);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<PatientResponseDto> getPatients() {
    return patientService.getPatients();
  }//bunda sorun var.

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{uniqueId}")
  public PatientResponseDto findPatientByUniqueId(@PathVariable UUID uniqueId) {
    return patientService.findPatientByUniqueId(uniqueId);
  }

  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/search-by-national-id")
  public PatientResponseDto findPatientByNationalId(@Valid @RequestBody FindPatientByNationalIdRequestDto findPatientByNationalIdRequestDto) {
      return patientService.findPatientByNationalId(findPatientByNationalIdRequestDto);
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/search")
  public List<PatientResponseDto> searchPatients(@RequestParam String query) { // Request Param'dan ayrıca bahsetmek gerekiyor.
   return patientService.searchPatient(query);
  }//bunda da sorun var.

  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{uniqueId}")
  public UpdatePatientResponseDto updatePatient(@PathVariable UUID uniqueId, @Valid @RequestBody UpdatePatientRequestDto requestDto) {
    return patientService.updatePatient(uniqueId, requestDto);
  }
}

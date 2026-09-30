package com.healthflow.presentation.controller;

import com.healthflow.application.service.PatientService;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.presentation.dto.patient.CreatePatientRequestDto;
import com.healthflow.presentation.dto.patient.FindPatientByNationalIdRequestDto;
import com.healthflow.presentation.dto.patient.PatientResponseDto;
import com.healthflow.presentation.dto.patient.UpdatePatientRequestDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
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
  } // Response DtO'yu neden kullanıyoruz?

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{uniqueId}")
  public PatientResponseDto findPatientByUniqueId(@PathVariable UUID uniqueId) {
    Patient patient = findPatientByUniqueIdUseCase.execute(uniqueId);

    User user = patient.getUser();
    return new PatientResponseDto(
        user.getUniqueId(), user.getName(), user.getSurname(), user.getBirthDate());
  }

  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/search-by-national-id")
  public PatientResponseDto findPatientByNationalId(
      @Valid @RequestBody FindPatientByNationalIdRequestDto findPatientByNationalIdRequestDto) {
    NationalId nationalId = new NationalId(findPatientByNationalIdRequestDto.nationalId());

    Patient patient = findPatientByNationalIdUseCase.execute(nationalId);
    User user = patient.getUser();
    return new PatientResponseDto(
        user.getUniqueId(), user.getName(), user.getSurname(), user.getBirthDate());
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/search")
  public List<PatientResponseDto> searchPatients(
      @RequestParam String query) { // Request Param'dan ayrıca bahsetmek gerekiyor.
    List<Patient> patients = searchPatientUseCase.execute(query);
    return patients.stream()
        .map(
            patient -> {
              User user = patient.getUser();

              return new PatientResponseDto(
                  user.getUniqueId(), user.getName(), user.getSurname(), user.getBirthDate());
            })
        .toList();
  }

  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{uniqueId}")
  public PatientResponseDto updatePatient(
      @PathVariable UUID uniqueId, @RequestBody UpdatePatientRequestDto requestDto) {
    Patient patient =
        updatePatientUseCase.execute(
            uniqueId, requestDto.email(), requestDto.phoneNumber(), requestDto.bloodType());

    User user = patient.getUser();
    return new PatientResponseDto(
        user.getUniqueId(), user.getName(), user.getSurname(), user.getBirthDate());
  }
}

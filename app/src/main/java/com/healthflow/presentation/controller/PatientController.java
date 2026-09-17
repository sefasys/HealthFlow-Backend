package com.healthflow.presentation.controller;


import com.healthflow.application.usecase.patient.*;
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

  private final CreatePatientUseCase createPatientUseCase;
  private final GetPatientsUseCase getPatientsUseCase;
  private final FindPatientByUniqueIdUseCase findPatientByUniqueIdUseCase;
  private final FindPatientByNationalIdUseCase findPatientByNationalIdUseCase;
  private final SearchPatientUseCase searchPatientUseCase;
  private final UpdatePatientUseCase updatePatientUseCase;

  public PatientController(
      CreatePatientUseCase createPatientUseCase,
      GetPatientsUseCase getPatientsUseCase,
      FindPatientByUniqueIdUseCase findPatientByUniqueIdUseCase,
      FindPatientByNationalIdUseCase findPatientByNationalIdUseCase,
      SearchPatientUseCase searchPatientUseCase,
      UpdatePatientUseCase updatePatientUseCase) {
    this.createPatientUseCase = createPatientUseCase;
    this.getPatientsUseCase = getPatientsUseCase;
    this.findPatientByUniqueIdUseCase = findPatientByUniqueIdUseCase;
    this.findPatientByNationalIdUseCase = findPatientByNationalIdUseCase;
    this.searchPatientUseCase = searchPatientUseCase;
    this.updatePatientUseCase = updatePatientUseCase;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PatientResponseDto createPatient(@Valid @RequestBody CreatePatientRequestDto requestDto) {
    NationalId nationalId = new NationalId(requestDto.nationalId());
    Patient patient =
        createPatientUseCase.execute(
            nationalId,
            requestDto.name(),
            requestDto.surname(),
            requestDto.birthDate(),
            requestDto.email(),
            requestDto.phoneNumber());
    User user = patient.getUser();
    PatientResponseDto responseDto =
        new PatientResponseDto(
            user.getUniqueId(), user.getName(), user.getSurname(), user.getBirthDate());
    return responseDto;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<PatientResponseDto> getPatients() {
    List<Patient> patients = getPatientsUseCase.execute();
    return patients.stream()
        .map(
            patient -> {
              User user = patient.getUser();

              return new PatientResponseDto(
                  user.getUniqueId(),
                  user.getName(),
                  user.getSurname(),
                  user.getBirthDate()); // Buradaki yapıya bir göz at.
            })
        .toList();
  }

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
  public PatientResponseDto findPatientByNationalId(@Valid @RequestBody FindPatientByNationalIdRequestDto findPatientByNationalIdRequestDto){
      NationalId nationalId = new NationalId(findPatientByNationalIdRequestDto.nationalId());

      Patient patient = findPatientByNationalIdUseCase.execute(nationalId);
      User user = patient.getUser();
      return new PatientResponseDto(user.getUniqueId(), user.getName(), user.getSurname(), user.getBirthDate());
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/search")
  public List<PatientResponseDto> searchPatients(@RequestParam String query){
      List<Patient> patients = searchPatientUseCase.execute(query);
      return patients.stream().map(patient -> {
          User user = patient.getUser();

          return new PatientResponseDto(
                  user.getUniqueId(),
                  user.getName(),
                  user.getSurname(),
                  user.getBirthDate()
          );
      }).toList();
  }

  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{uniqueId}")
  public PatientResponseDto updatePatient(@PathVariable UUID uniqueId,
                                          @RequestBody UpdatePatientRequestDto requestDto){
      Patient patient = updatePatientUseCase.execute(uniqueId,
              requestDto.email(),
              requestDto.phoneNumber(),
              requestDto.bloodType()
      );

      User user = patient.getUser();
      return new PatientResponseDto(user.getUniqueId(), user.getName(), user.getSurname(), user.getBirthDate());
  }
}

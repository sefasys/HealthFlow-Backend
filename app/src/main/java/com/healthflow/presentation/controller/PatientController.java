package com.healthflow.presentation.controller;

import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.application.usecase.patient.CreatePatientUseCase;
import com.healthflow.application.usecase.patient.FindPatientByUniqueIdUseCase;
import com.healthflow.application.usecase.patient.GetPatientsUseCase;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.presentation.dto.patient.CreatePatientRequestDto;
import com.healthflow.presentation.dto.patient.PatientResponseDto;
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

  public PatientController(
      CreatePatientUseCase createPatientUseCase,
      GetPatientsUseCase getPatientsUseCase,
      FindPatientByUniqueIdUseCase findPatientByUniqueIdUseCase) {
    this.createPatientUseCase = createPatientUseCase;
    this.getPatientsUseCase = getPatientsUseCase;
    this.findPatientByUniqueIdUseCase = findPatientByUniqueIdUseCase;
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
            user.getUniqueID(), user.getName(), user.getSurname(), user.getBirthDate());
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
                  user.getUniqueID(),
                  user.getName(),
                  user.getSurname(),
                  user.getBirthDate()); // Buradaki yapıya bir göz at.
            })
        .toList();
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{uniqueId}")
  public PatientResponseDto findPatientByUniqueId(@PathVariable UUID uniqueId) {
    Patient patient =
        findPatientByUniqueIdUseCase
            .execute(uniqueId)
            .orElseThrow(
                () ->
                    new PatientNotFoundException(
                        "Patient not found with unique id: "
                            + uniqueId) // or else throw yapmak zorundaydık çünkü Patient aslında
                // optional<patient>.
                );
    User user = patient.getUser();
    return new PatientResponseDto(
        user.getUniqueID(), user.getName(), user.getSurname(), user.getBirthDate());
  }
}

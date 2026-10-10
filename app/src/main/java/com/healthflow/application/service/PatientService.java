package com.healthflow.application.service;

import com.healthflow.application.dto.patient.*;
import com.healthflow.application.exception.InvalidSearchQueryException;
import com.healthflow.application.exception.InvalidUpdateRequestException;
import com.healthflow.application.exception.PatientAlreadyExistsException;
import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.application.mapper.patient.PatientMapper;
import com.healthflow.domain.exception.InvalidNationalIdException;
import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    // Şu anlık @Autowired kullanmamıza gerek yok çünkü tek constructorlar üzerinden ilerliyoruz.
    private final IPatientRepository patientRepository;
    private final UserService userService;
    private final PatientMapper patientMapper;


    public PatientService(IPatientRepository patientRepository, UserService userService, PatientMapper patientMapper){
        this.patientRepository = patientRepository;
        this.userService = userService;
        this.patientMapper = patientMapper;
    }

    public PatientResponseDto createPatient(CreatePatientRequestDto requestDto) {
        NationalId nationalId = new NationalId(requestDto.nationalId());

        if (patientRepository.findByNationalId(nationalId).isPresent()) {
            throw new PatientAlreadyExistsException(
                    "There is already a patient with the same national ID."
            );
        }

        User user = userService.resolveUser(
                nationalId,
                requestDto.name(),
                requestDto.surname(),
                requestDto.birthDate(),
                requestDto.email(),
                requestDto.phoneNumber(),
                UserRole.PATIENT
        );

        Patient patient = new Patient(user);

        user.addRole(UserRole.PATIENT);

        userService.saveUser(user);
        patientRepository.addPatient(patient);

        return patientMapper.responseDto(patient);
    }

    public List<PatientResponseDto> getPatients(){
        return patientRepository.getPatients().stream()
                .map(patientMapper::responseDto)
                .toList();
    }

    public PatientResponseDto findPatientByUniqueId(UUID uniqueId){
        if (uniqueId == null) {
            throw new InvalidUniqueIdException("Unique ID cannot be null.");
        }
        Patient patient = patientRepository
                .findByUniqueId(uniqueId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with unique id: " + uniqueId));

        return patientMapper.responseDto(patient);
    }

    public PatientResponseDto findPatientByNationalId(FindPatientByNationalIdRequestDto requestDto){
        if (requestDto.nationalId() == null) {
            throw new InvalidNationalIdException("National ID cannot be null.");
        }
        NationalId nationalId = new NationalId(requestDto.nationalId());
        Patient patient = patientRepository
                .findByNationalId(nationalId)
                .orElseThrow(
                        () -> new PatientNotFoundException("Patient not found with this national id."));

        return patientMapper.responseDto(patient);
    }

    public List<PatientResponseDto> searchPatient(String query){
        if (query == null || query.isBlank()) {
            throw new InvalidSearchQueryException("Search query cannot be null or blank.");
        }

        return patientRepository.search(query).stream().map(patientMapper::responseDto).toList();

    }


    public UpdatePatientResponseDto updatePatient(UUID uniqueId, UpdatePatientRequestDto updatePatientRequestDto){
        if (uniqueId == null) {
            throw new InvalidUniqueIdException("Unique id can not be null.");
        }

        if (updatePatientRequestDto.email() == null && updatePatientRequestDto.phoneNumber() == null && updatePatientRequestDto.bloodType() == null) {

            throw new InvalidUpdateRequestException("At least one field must be provided for update.");
        }

        Patient patient =
                patientRepository
                        .findByUniqueId(uniqueId)
                        .orElseThrow(() -> new PatientNotFoundException("Patient could not be found."));

        if (updatePatientRequestDto.email() != null) {
            patient.getUser().updateEmail(updatePatientRequestDto.email());
        }
        if (updatePatientRequestDto.phoneNumber() != null) {
            patient.getUser().updatePhoneNumber(updatePatientRequestDto.phoneNumber());
        }
        if (updatePatientRequestDto.bloodType() != null) {
            patient.updateBloodType(updatePatientRequestDto.bloodType());
        }
        patientRepository.update(patient);

        return patientMapper.updateResponseDto(patient);
    }





}

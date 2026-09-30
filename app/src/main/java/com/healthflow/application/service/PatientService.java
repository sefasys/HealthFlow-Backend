package com.healthflow.application.service;

import com.healthflow.application.exception.PatientAlreadyExistsException;
import com.healthflow.application.mapper.PatientMapper;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import com.healthflow.presentation.dto.patient.CreatePatientRequestDto;
import com.healthflow.presentation.dto.patient.PatientResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    // Şu anlık @Autowired kullanmamıza gerek yok çünkü tek constructorlar üzerinden ilerliyoruz.
    private final IPatientRepository patientRepository;
    private final UserFactory userFactory;
    private final PatientMapper patientMapper;

    public PatientService(IPatientRepository patientRepository, UserFactory userFactory, PatientMapper patientMapper){
        this.patientRepository = patientRepository;
        this.userFactory = userFactory;
        this.patientMapper = patientMapper;
    }

    public PatientResponseDto createPatient(CreatePatientRequestDto requestDto){
        Patient patient;
        NationalId nationalId = new NationalId(requestDto.nationalId());
        if (patientRepository.findByNationalId(nationalId).isPresent()) {
            throw new PatientAlreadyExistsException(
                    "There is already a user with the same national identity number.");
        }

            User user =
                    userFactory.createUser(
                            nationalId,
                            requestDto.name(),
                            requestDto.surname(),
                            requestDto.birthDate(),
                            requestDto.email(),
                            requestDto.phoneNumber(),
                            UserRole.PATIENT);
            patient = new Patient(user);

        patientRepository.addPatient(patient);
        return patientMapper.toResponseDto(patient);
    }

    public List<PatientResponseDto> getPatients(){
        return patientRepository.getPatients().stream()
                .map(patientMapper::toResponseDto)
                .toList();
    }







}

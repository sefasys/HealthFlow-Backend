package com.healthflow.service;

import com.healthflow.dto.common.PageResponse;
import com.healthflow.dto.patient.PatientRegistrationRequest;
import com.healthflow.dto.patient.PatientResponse;
import com.healthflow.dto.patient.UpdatePatientMedicalInfoRequest;
import com.healthflow.entity.user.Gender;
import com.healthflow.entity.user.User;
import com.healthflow.entity.user.UserRole;
import com.healthflow.entity.user.patient.BloodType;
import com.healthflow.entity.user.patient.Patient;
import com.healthflow.exception.PatientNotFoundException;
import com.healthflow.exception.UserAlreadyExistsException;
import com.healthflow.mapper.patient.PatientMapper;
import com.healthflow.repository.IPatientRepository;
import com.healthflow.repository.IUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Bu arkadaş daha çok tercih ediliyormuş çünkü daha fazla ayar sunuyor ama spring boot iki transactional anotasyonunu da tanıyormuş.

import java.util.UUID;

@Service
public class PatientService {

    private final IUserRepository userRepository;
    private final IPatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientService(IUserRepository userRepository, IPatientRepository patientRepository, PatientMapper patientMapper) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Transactional
    public PatientResponse register(PatientRegistrationRequest request){
        if (userRepository.existsByNationalId(request.nationalId())) {
            throw new UserAlreadyExistsException("A user with this national ID already exists");
        }
        if (userRepository.existsByEmail(request.email().toLowerCase())) {
            throw new UserAlreadyExistsException("A user with this email already exists");
        }

        User user = patientMapper.toUser(request);
        user.addRole(UserRole.PATIENT);
        userRepository.save(user);

        Patient patient = new Patient(user);
        if (request.bloodType() != null) {
            patient.updateBloodType(request.bloodType());
        }
        if (request.allergies() != null) {
            request.allergies().forEach(patient::addAllergy);
        }
        patientRepository.save(patient);

        return patientMapper.toResponse(patient);

    }

    @Transactional(readOnly = true) //read-only nedir?
    public PatientResponse getById(UUID id) {
        Patient patient = findPatient(id);
        return patientMapper.toResponse(patient);
    }

    @Transactional
    public PatientResponse updateMedicalInfo(UUID id, UpdatePatientMedicalInfoRequest request) {
        Patient patient = findPatient(id);

        if (request.bloodType() != null) {
            patient.updateBloodType(request.bloodType());
        }
        if (request.allergies() != null) {
            patient.replaceAllergies(request.allergies());
        }

        return patientMapper.toResponse(patient);
    }

    private Patient findPatient(UUID id) {
        return patientRepository.findWithDetailsById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with: " + id));
    }


    @Transactional(readOnly = true)
    public PatientResponse getByNationalId(String nationalId) {
        return patientRepository.findByUserNationalId(nationalId)
                .map(patientMapper::toResponse)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with this national ID."));
    }

    @Transactional(readOnly = true)
    public PageResponse<PatientResponse> search(String query, Gender gender,
                                                BloodType bloodType, Pageable pageable) {
        String normalizedQuery = (query == null) ? "" : query.trim();

        Page<PatientResponse> page = patientRepository
                .search(normalizedQuery, gender, bloodType, pageable)
                .map(patientMapper::toResponse);

        return PageResponse.from(page);
    }

}

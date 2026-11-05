package com.healthflow.service;

import com.healthflow.dto.patient.PatientRegistrationRequest;
import com.healthflow.dto.patient.PatientResponse;
import com.healthflow.entity.user.User;
import com.healthflow.mapper.patient.PatientMapper;
import com.healthflow.repository.IPatientRepository;
import com.healthflow.repository.IUserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Bu arkadaş daha çok tercih ediliyormuş çünkü daha fazla ayar sunuyor ama spring boot iki transactional anotasyonunu da tanıyormuş.

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
        if(userRepository.existsByNationalId(request.nationalId())){

        }
        if(userRepository.existsByEmail(request.email())){

        }

        User user = patientMapper.to
    }

}

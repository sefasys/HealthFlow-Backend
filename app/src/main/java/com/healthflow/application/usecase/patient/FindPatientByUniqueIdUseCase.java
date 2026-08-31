package com.healthflow.application.usecase.patient;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.PatientRepository;

import java.util.Optional;

public class FindPatientByUniqueIdUseCase {
    private final PatientRepository patientRepository;

    FindPatientByUniqueIdUseCase(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    public Optional<Patient> execute(Long id){
        return patientRepository.findByUniqueId(id);
    }
}

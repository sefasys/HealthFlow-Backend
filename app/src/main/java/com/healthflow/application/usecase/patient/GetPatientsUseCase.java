package com.healthflow.application.usecase.patient;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.PatientRepository;

import java.util.List;

public class GetPatientsUseCase {

    private final PatientRepository patientRepository;

    public GetPatientsUseCase(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    public List<Patient> execute(){
        return patientRepository.getPatients();
    }

}

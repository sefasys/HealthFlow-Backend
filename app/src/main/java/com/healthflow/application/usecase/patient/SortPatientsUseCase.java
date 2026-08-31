package com.healthflow.application.usecase.patient;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.PatientRepository;
import com.healthflow.port.repository.PatientSortType;

import java.util.List;

public class SortPatientsUseCase {
    private final PatientRepository patientRepository;

    SortPatientsUseCase(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    public List<Patient> execute(PatientSortType sortType){
        return patientRepository.sort(sortType);
    }
}

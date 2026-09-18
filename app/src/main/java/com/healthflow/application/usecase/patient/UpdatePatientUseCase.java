package com.healthflow.application.usecase.patient;

import com.healthflow.application.exception.InvalidUpdateRequestException;
import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.domain.model.user.patient.BloodType;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;


import java.util.UUID;

public class UpdatePatientUseCase {

    private final IPatientRepository patientRepository; //Spring'le ve in memory ile alakalı hiçbir şey yok.

    public UpdatePatientUseCase(IPatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    public Patient execute(
            UUID uniqueId,
            String email,
            String phoneNumber,
            BloodType bloodType
    ){
        if(uniqueId == null){
            throw new InvalidUniqueIdException("Unique id can not be null.");
        }

        if (email == null
                && phoneNumber == null
                && bloodType == null) {

            throw new InvalidUpdateRequestException(
                    "At least one field must be provided for update."
            );
        }

        Patient patient = patientRepository.findByUniqueId(uniqueId).orElseThrow(()
                -> new PatientNotFoundException("Patient could not found."));


        if(email != null){
            patient.updateEmail(email);
        }
        if(phoneNumber != null){
            patient.updatePhoneNumber(phoneNumber);
        }
        if(bloodType != null){
            patient.updateBloodType(bloodType);
        }
        patientRepository.update(patient);
        return patient;
    }
}

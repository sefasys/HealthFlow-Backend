package com.healthflow.service;

import com.healthflow.mapper.patient.PatientMapper;
import com.healthflow.domain.exception.InvalidNationalIdException;
import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.repository.IPatientRepository;
import org.springframework.stereotype.Service;

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


}

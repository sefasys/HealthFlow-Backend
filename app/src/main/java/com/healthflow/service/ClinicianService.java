package com.healthflow.service;

import com.healthflow.mapper.clinician.ClinicianMapper;
import com.healthflow.domain.exception.InvalidNationalIdException;
import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.entity.factory.StaffFactory;
import com.healthflow.repository.IClinicianRepository;
import org.springframework.stereotype.Service;

@Service
public class ClinicianService {

    private final IClinicianRepository clinicianRepository;
    private final UserService userService;
    private final StaffFactory staffFactory;
    private final ClinicianMapper clinicianMapper;

    public ClinicianService(IClinicianRepository clinicianRepository, UserService userService, ClinicianMapper clinicianMapper, StaffFactory staffFactory){
        this.clinicianRepository = clinicianRepository;
        this.userService = userService;
        this.clinicianMapper = clinicianMapper;
        this.staffFactory = staffFactory;
    }


}

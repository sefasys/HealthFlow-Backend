package com.healthflow.service;

import com.healthflow.mapper.availability.AvailabilityMapper;
import com.healthflow.domain.exception.InvalidAvailabilityException;
import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.repository.IAvailabilityRepository;
import com.healthflow.repository.IClinicRegistrarRepository;
import com.healthflow.repository.IClinicianRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class AvailabilityService {

    private final IAvailabilityRepository availabilityRepository;
    private final AvailabilityMapper availabilityMapper;
    private final Clock clock;
    private final IClinicianRepository clinicianRepository;
    private final IClinicRegistrarRepository clinicRegistrarRepository;

    public AvailabilityService(IAvailabilityRepository availabilityRepository, AvailabilityMapper availabilityMapper, Clock clock, IClinicianRepository clinicianRepository, IClinicRegistrarRepository clinicRegistrarRepository){
        this.availabilityRepository = availabilityRepository;
        this.availabilityMapper = availabilityMapper;
        this.clock = clock;
        this.clinicianRepository = clinicianRepository;
        this.clinicRegistrarRepository = clinicRegistrarRepository;
    }


}


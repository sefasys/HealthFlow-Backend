package com.healthflow.application.service;

import com.healthflow.port.repository.IAvailabilityRepository;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityService {

    private final IAvailabilityRepository availabilityRepository;

    public AvailabilityService(IAvailabilityRepository availabilityRepository){
        this.availabilityRepository = availabilityRepository;
    }



}


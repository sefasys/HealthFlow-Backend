package com.healthflow.service;


import com.healthflow.dto.availability.AvailabilityResponse;
import com.healthflow.dto.availability.CreateAvailabilityRequest;
import com.healthflow.entity.availability.Availability;
import com.healthflow.entity.availability.AvailabilityStatus;
import com.healthflow.entity.user.staff.Clinician;
import com.healthflow.exception.AvailabilityConflictException;
import com.healthflow.exception.AvailabilityNotFoundException;
import com.healthflow.exception.ClinicianNotFoundException;
import com.healthflow.exception.InvalidAvailabilityException;
import com.healthflow.mapper.availability.AvailabilityMapper;
import com.healthflow.repository.IAvailabilityRepository;
import com.healthflow.repository.IClinicianRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AvailabilityService {

    private final IAvailabilityRepository availabilityRepository;
    private final IClinicianRepository clinicianRepository;
    private final AvailabilityMapper availabilityMapper;

    public AvailabilityService(IAvailabilityRepository availabilityRepository, IClinicianRepository clinicianRepository, AvailabilityMapper availabilityMapper) {
        this.availabilityRepository = availabilityRepository;
        this.clinicianRepository = clinicianRepository;
        this.availabilityMapper = availabilityMapper;
    }

    @Transactional
    public AvailabilityResponse create(UUID clinicianId, CreateAvailabilityRequest request) {
        Clinician clinician = clinicianRepository.findWithDetailsById(clinicianId)
                .orElseThrow(() -> new ClinicianNotFoundException("Clinician not found with id: " + clinicianId));

        if (!clinician.getStaff().isActive()) {
            throw new InvalidAvailabilityException("Only active clinicians can create availability");
        }

        if (availabilityRepository.existsOverlapping(clinicianId,
                request.startTime(), request.endTime(), AvailabilityStatus.ACTIVE)) {
            throw new AvailabilityConflictException("Overlaps with an existing availability");
        }

        Availability availability = new Availability(clinician, request.startTime(), request.endTime(), request.slotDuration());
        availabilityRepository.save(availability);

        return availabilityMapper.toResponse(availability);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getActiveByClinician(UUID clinicianId) {
        if (!clinicianRepository.existsById(clinicianId)) {
            throw new ClinicianNotFoundException("Clinician not found with the ID: " + clinicianId);
        }
        return availabilityRepository.findByClinicianIdAndStatusAndEndTimeAfterOrderByStartTime(
                        clinicianId, AvailabilityStatus.ACTIVE, LocalDateTime.now())
                .stream()
                .map(availabilityMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AvailabilityResponse getById(UUID availabilityId) {
        return availabilityRepository.findById(availabilityId)
                .map(availabilityMapper::toResponse)
                .orElseThrow(() -> new AvailabilityNotFoundException("Availability not found with ID: " + availabilityId));
    }
}


package com.healthflow.service;


import com.healthflow.dto.clinician.ClinicianRegistrationRequest;
import com.healthflow.dto.clinician.ClinicianResponse;
import com.healthflow.entity.user.User;
import com.healthflow.entity.user.UserRole;
import com.healthflow.entity.user.staff.Clinician;
import com.healthflow.entity.user.staff.Specialty;
import com.healthflow.entity.user.staff.Staff;
import com.healthflow.exception.ClinicianAlreadyExistsException;
import com.healthflow.exception.ClinicianNotFoundException;
import com.healthflow.mapper.clinician.ClinicianMapper;
import com.healthflow.repository.IClinicianRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class ClinicianService {

    private final IClinicianRepository clinicianRepository;
    private final StaffService staffService;
    private final ClinicianMapper clinicianMapper;

    public ClinicianService(IClinicianRepository clinicianRepository, StaffService staffService, ClinicianMapper clinicianMapper) {
        this.clinicianRepository = clinicianRepository;
        this.staffService = staffService;
        this.clinicianMapper = clinicianMapper;
    }

    @Transactional
    public ClinicianResponse register(ClinicianRegistrationRequest request) {
        if (clinicianRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new ClinicianAlreadyExistsException("License number already in use");
        }

        User user = clinicianMapper.toUser(request);
        Staff staff = staffService.createStaff(user, UserRole.CLINICIAN,
                request.employeeNumber(), request.hireDate());

        Clinician clinician = new Clinician(staff, request.specialty(), request.licenseNumber());
        clinicianRepository.save(clinician);

        return clinicianMapper.toResponse(clinician);
    }

    @Transactional(readOnly = true)
    public ClinicianResponse getById(UUID id) {
        return clinicianRepository.findWithDetailsById(id)
                .map(clinicianMapper::toResponse)
                .orElseThrow(() -> new ClinicianNotFoundException("Clinician not found with the unique id: "+ id));
    }

    @Transactional(readOnly = true)
    public List<ClinicianResponse> getActiveBySpecialty(Specialty specialty) {
        return clinicianRepository.findActiveBySpecialty(specialty).stream()
                .map(clinicianMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClinicianResponse getByLicenseNumber(String licenseNumber) {
        return clinicianRepository.findByLicenseNumber(licenseNumber)
                .map(clinicianMapper::toResponse)
                .orElseThrow(() -> new ClinicianNotFoundException("Clinician not found"));
    }

    @Transactional(readOnly = true)
    public ClinicianResponse getByEmployeeNumber(String employeeNumber) {
        return clinicianRepository.findByStaffEmployeeNumber(employeeNumber)
                .map(clinicianMapper::toResponse)
                .orElseThrow(() -> new ClinicianNotFoundException("Clinician not found"));
    }



}

package com.healthflow.service;


import com.healthflow.dto.clinicregistrar.ClinicRegistrarRegistrationRequest;
import com.healthflow.dto.clinicregistrar.ClinicRegistrarResponse;
import com.healthflow.entity.user.User;
import com.healthflow.entity.user.UserRole;
import com.healthflow.entity.user.staff.ClinicRegistrar;
import com.healthflow.entity.user.staff.Staff;
import com.healthflow.exception.ClinicRegistrarNotFoundException;
import com.healthflow.mapper.clinicregistrar.ClinicRegistrarMapper;
import com.healthflow.repository.IClinicRegistrarRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class ClinicRegistrarService {

    private final IClinicRegistrarRepository registrarRepository;
    private final ClinicRegistrarMapper registrarMapper;
    private final StaffService staffService;

    public ClinicRegistrarService( IClinicRegistrarRepository registrarRepository, ClinicRegistrarMapper registrarMapper, StaffService staffService) {
        this.registrarRepository = registrarRepository;
        this.registrarMapper = registrarMapper;
        this.staffService = staffService;
    }

    @Transactional
    public ClinicRegistrarResponse register(ClinicRegistrarRegistrationRequest request) {
        User user = registrarMapper.toUser(request);
        Staff staff = staffService.createStaff(user, UserRole.CLINIC_REGISTRAR,
                request.employeeNumber(), request.hireDate());

        ClinicRegistrar registrar = new ClinicRegistrar(staff);
        registrarRepository.save(registrar);

        return registrarMapper.toResponse(registrar);
    }

    @Transactional(readOnly = true)
    public ClinicRegistrarResponse getById(UUID id) {
        return registrarRepository.findWithDetailsById(id)
                .map(registrarMapper::toResponse)
                .orElseThrow(() -> new ClinicRegistrarNotFoundException("Clinic registrar with the " + id));
    }



}

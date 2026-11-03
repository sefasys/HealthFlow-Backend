package com.healthflow.service;

import com.healthflow.application.exception.*;
import com.healthflow.mapper.clinicregistrar.ClinicRegistrarMapper;
import com.healthflow.domain.exception.InvalidNationalIdException;
import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.entity.factory.StaffFactory;
import com.healthflow.entity.user.NationalId;
import com.healthflow.entity.user.User;
import com.healthflow.entity.user.UserRole;
import com.healthflow.entity.user.staff.ClinicRegistrar;
import com.healthflow.entity.user.staff.Staff;
import com.healthflow.dto.clinicregistrar.*;
import com.healthflow.repository.IClinicRegistrarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClinicRegistrarService {

    private final IClinicRegistrarRepository clinicRegistrarRepository;
    private final UserService userService;
    private final StaffFactory staffFactory;
    private final ClinicRegistrarMapper clinicRegistrarMapper;


    public ClinicRegistrarService(IClinicRegistrarRepository clinicRegistrarRepository, UserService userService, StaffFactory staffFactory, ClinicRegistrarMapper clinicRegistrarMapper) {
        this.clinicRegistrarRepository = clinicRegistrarRepository;
        this.userService = userService;
        this.staffFactory = staffFactory;
        this.clinicRegistrarMapper = clinicRegistrarMapper;

    }


}

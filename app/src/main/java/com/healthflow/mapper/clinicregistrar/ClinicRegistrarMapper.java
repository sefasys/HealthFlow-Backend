package com.healthflow.mapper.clinicregistrar;

import com.healthflow.dto.clinicregistrar.ClinicRegistrarRegistrationRequest;
import com.healthflow.dto.clinicregistrar.ClinicRegistrarResponse;
import com.healthflow.entity.user.User;
import com.healthflow.entity.user.staff.ClinicRegistrar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClinicRegistrarMapper{

        @Mapping(target = ".", source = "staff.user")
        @Mapping(target = "employeeNumber", source = "staff.employeeNumber")
        @Mapping(target = "hireDate", source = "staff.hireDate")
        @Mapping(target = "employmentStatus", source = "staff.employmentStatus")
        ClinicRegistrarResponse toResponse(ClinicRegistrar registrar);

        User toUser(ClinicRegistrarRegistrationRequest request);
}

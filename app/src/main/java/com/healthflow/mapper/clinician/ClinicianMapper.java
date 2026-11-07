package com.healthflow.mapper.clinician;

import com.healthflow.dto.clinician.ClinicianRegistrationRequest;
import com.healthflow.dto.clinician.ClinicianResponse;
import com.healthflow.entity.user.User;
import com.healthflow.entity.user.staff.Clinician;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClinicianMapper {

    @Mapping(target = ".", source = "staff.user")
    @Mapping(target = "employeeNumber", source = "staff.employeeNumber")
    @Mapping(target = "hireDate", source = "staff.hireDate")
    @Mapping(target = "employmentStatus", source = "staff.employmentStatus")
    ClinicianResponse toResponse(Clinician clinician);

    User toUser(ClinicianRegistrationRequest request);

}

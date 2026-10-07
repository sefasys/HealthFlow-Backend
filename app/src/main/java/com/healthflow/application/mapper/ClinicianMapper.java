package com.healthflow.application.mapper;

import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.application.dto.clinician.ClinicianResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClinicianMapper {

    @Mapping(source = "staff.user.uniqueId", target = "id")
    @Mapping(source = "staff.user.name", target = "name")
    @Mapping(source = "staff.user.surname", target = "surname")
    @Mapping(source = "staff.user.birthDate", target = "birthDate")
    @Mapping(source = "staff.hireDate", target = "hireDate")
    @Mapping(source = "staff.employmentStatus", target = "employmentStatus")
    ClinicianResponseDto responseDto(Clinician clinician);
}

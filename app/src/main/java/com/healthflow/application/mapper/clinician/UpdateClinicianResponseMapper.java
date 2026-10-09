package com.healthflow.application.mapper.clinician;

import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.application.dto.clinician.UpdateClinicianResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UpdateClinicianResponseMapper {

    @Mapping(source = "staff.user.uniqueId", target = "id")
    @Mapping(source = "staff.user.name", target = "name")
    @Mapping(source = "staff.user.surname", target = "surname")
    @Mapping(source = "staff.user.birthDate", target = "birthDate")
    @Mapping(source = "staff.user.email", target = "email")
    @Mapping(source = "staff.user.phoneNumber", target = "phoneNumber")
    UpdateClinicianResponseDto responseDto(Clinician clinician);
}

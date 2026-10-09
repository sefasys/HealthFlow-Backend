package com.healthflow.application.mapper.clinicregistrar;

import com.healthflow.application.dto.clinicregistrar.UpdateClinicRegistrarResponseDto;
import com.healthflow.domain.model.user.staff.ClinicRegistrar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UpdateClinicRegistrarResponseMapper {
    @Mapping(source = "staff.user.uniqueId", target = "id")
    @Mapping(source = "staff.user.name", target = "name")
    @Mapping(source = "staff.user.surname", target = "surname")
    @Mapping(source = "staff.user.birthDate", target = "birthDate")
    @Mapping(source = "staff.user.email", target = "email")
    @Mapping(source = "staff.user.phoneNumber", target = "phoneNumber")
    UpdateClinicRegistrarResponseDto responseDto(ClinicRegistrar clinicRegistrar);
}

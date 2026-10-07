package com.healthflow.application.mapper;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.application.dto.patient.UpdatePatientResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UpdatePatientResponseMapper {

    @Mapping(source = "user.uniqueId", target = "id")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.surname", target = "surname")
    @Mapping(source = "user.birthDate", target = "birthDate")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    @Mapping(source = "bloodType", target = "bloodType")
    UpdatePatientResponseDto responseDto(Patient patient);


}

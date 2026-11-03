package com.healthflow.mapper.patient;

import com.healthflow.dto.patient.UpdatePatientResponseDto;
import com.healthflow.entity.user.patient.Patient;
import com.healthflow.dto.patient.PatientResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring")
public interface PatientMapper {

        @Mapping(source = "user.uniqueId", target = "id")
        @Mapping(source = "user.name", target = "name")
        @Mapping(source = "user.surname", target = "surname")
        @Mapping(source = "user.birthDate", target = "birthDate")
        PatientResponseDto responseDto(Patient patient);
        // Bu kısma çalış. Aslında bir converter olarak çalışıyor MapStruct Anotasyonu

    @Mapping(source = "user.uniqueId", target = "id")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.surname", target = "surname")
    @Mapping(source = "user.birthDate", target = "birthDate")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    @Mapping(source = "bloodType", target = "bloodType")
    UpdatePatientResponseDto updateResponseDto(Patient patient);
}

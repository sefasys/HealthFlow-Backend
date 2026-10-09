package com.healthflow.application.mapper.patient;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.application.dto.patient.PatientResponseDto;
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

}

package com.healthflow.mapper.patient;

import com.healthflow.dto.patient.PatientRegistrationRequest;
import com.healthflow.entity.user.User;
import com.healthflow.entity.user.patient.Patient;
import com.healthflow.dto.patient.PatientResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = ".", source = "user")
    PatientResponse toResponse(Patient patient);

    User toUser(PatientRegistrationRequest request);
}

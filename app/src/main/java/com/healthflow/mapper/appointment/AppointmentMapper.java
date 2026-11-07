package com.healthflow.mapper.appointment;

import com.healthflow.dto.appointment.AppointmentResponse;
import com.healthflow.entity.appointment.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "availabilityId", source = "availability.id")
    @Mapping(target = "clinicianId", source = "availability.clinician.id")
    @Mapping(target = "patientId", source = "patient.id")
    AppointmentResponse toResponse(Appointment appointment);
}
package com.healthflow.application.mapper.appointment;

import com.healthflow.application.dto.appointment.AppointmentResponseDto;
import com.healthflow.application.dto.appointment.AppointmentSlotResponseDto;
import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.appointment.AppointmentSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "patientId", source = "patient.user.uniqueId")
    @Mapping(target = "clinicianId", source = "clinician.staff.user.uniqueId")
    @Mapping(target = "startTime", source = "timeRange.start")
    @Mapping(target = "endTime", source = "timeRange.end")
    AppointmentResponseDto responseDto(Appointment appointment);

    List<AppointmentResponseDto> responseDtoList(
            List<Appointment> appointments
    );

    @Mapping(target = "startTime", source = "timeRange.start")
    @Mapping(target = "endTime", source = "timeRange.end")
    AppointmentSlotResponseDto slotResponseDto(AppointmentSlot slot);

    List<AppointmentSlotResponseDto> slotResponseDtoList(
            List<AppointmentSlot> slots
    );
}
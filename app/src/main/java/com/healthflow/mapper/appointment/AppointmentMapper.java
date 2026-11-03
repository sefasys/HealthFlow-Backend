package com.healthflow.mapper.appointment;

import com.healthflow.dto.appointment.AppointmentResponseDto;
import com.healthflow.dto.appointment.AppointmentSlotResponseDto;
import com.healthflow.entity.appointment.Appointment;
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
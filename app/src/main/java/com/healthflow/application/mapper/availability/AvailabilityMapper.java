package com.healthflow.application.mapper.availability;

import com.healthflow.application.dto.availability.AvailabilityResponseDto;
import com.healthflow.domain.model.appointment.Availability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {

    @Mapping(target = "clinicianId", source = "clinician.staff.user.uniqueId")
    @Mapping(target = "startTime", source = "timeRange.start")
    @Mapping(target = "endTime", source = "timeRange.end")
    AvailabilityResponseDto responseDto(Availability availability);

    List<AvailabilityResponseDto> toResponseDtoList(
            List<Availability> availabilities
    );
}

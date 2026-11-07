package com.healthflow.mapper.availability;

import com.healthflow.dto.availability.AvailabilityResponse;
import com.healthflow.entity.availability.Availability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {

    @Mapping(target = "clinicianId", source = "clinician.id")
    AvailabilityResponse toResponse(Availability availability);
}

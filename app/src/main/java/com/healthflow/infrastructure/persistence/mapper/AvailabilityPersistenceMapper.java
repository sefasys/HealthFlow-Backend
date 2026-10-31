package com.healthflow.infrastructure.persistence.mapper;

import com.healthflow.domain.model.appointment.Availability;
import com.healthflow.domain.model.appointment.TimeRange;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.infrastructure.persistence.entity.AvailabilityEntity;
import com.healthflow.infrastructure.persistence.entity.StaffEntity;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityPersistenceMapper {

    private final StaffPersistenceMapper mapper;

    public AvailabilityPersistenceMapper(StaffPersistenceMapper mapper) {
        this.mapper = mapper;
    }

    public AvailabilityEntity toEntity(Availability availability, StaffEntity staff){
        return new AvailabilityEntity(
                availability.getUniqueId(),
                availability.getRejectionReason(),
                availability.getReviewedAt(),
                availability.getReviewedByUserId(),
                availability.getStatus(),
                availability.getTimeRange().end(),
                availability.getTimeRange().start(),
                availability.getDate(),
                staff
        );
    }

    public Availability toDomain(AvailabilityEntity entity) {
        return Availability.restore(
                entity.getUniqueId(),
                new Clinician(mapper.toDomain(entity.getStaff())),
                entity.getWorkDate(),
                new TimeRange(entity.getStartTime(), entity.getEndTime()),
                entity.getStatus(),
                entity.getReviewedBy(),
                entity.getReviewedAt(),
                entity.getRejectionReason()
        );
    }

}

package com.healthflow.infrastructure.persistence.mapper;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.appointment.TimeRange;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.infrastructure.persistence.entity.AppointmentEntity;
import com.healthflow.infrastructure.persistence.entity.PatientEntity;
import com.healthflow.infrastructure.persistence.entity.StaffEntity;
import org.springframework.stereotype.Component;

@Component
public class AppointmentPersistenceMapper {

    private PatientPersistenceMapper patientMapper;
    private StaffPersistenceMapper staffMapper;

    public AppointmentPersistenceMapper(StaffPersistenceMapper staffMapper, PatientPersistenceMapper patientMapper) {
        this.staffMapper = staffMapper;
        this.patientMapper = patientMapper;
    }

    public AppointmentEntity toEntity(Appointment appointment, PatientEntity patient, StaffEntity clinician){
        return new AppointmentEntity(
                appointment.getUniqueId(),
                appointment.getAvailabilityId(),
                patient,
                clinician,
                appointment.getDate(),
                appointment.getTimeRange().start(),
                appointment.getTimeRange().end(),
                appointment.getStatus()
        );
    }

    public Appointment toDomain(AppointmentEntity entity){
        return Appointment.restore(
                entity.getUniqueId(),
                patientMapper.toDomain(entity.getPatient()),
                new Clinician(staffMapper.toDomain(entity.getClinician())),
                entity.getAvailabilityId(),
                entity.getDate(),
                new TimeRange(entity.getStartTime(), entity.getEndTime()),
                entity.getStatus()
        );
    }

}

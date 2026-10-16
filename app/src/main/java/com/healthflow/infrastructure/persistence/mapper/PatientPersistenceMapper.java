package com.healthflow.infrastructure.persistence.mapper;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.infrastructure.persistence.entity.PatientEntity;
import com.healthflow.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class PatientPersistenceMapper {

    private final UserPersistenceMapper userMapper;

    public PatientPersistenceMapper(UserPersistenceMapper userMapper) {
        this.userMapper = userMapper;
    }

    // PatientPersistenceMapper
    public PatientEntity toEntity(Patient patient, UserEntity user) {
        return new PatientEntity(user, patient.getUniqueId(), patient.getBloodType());
    }
// userMapper.toEntity çağrısı kalktı, toDomain aynı kalıyor

    public Patient toDomain(PatientEntity entity) {
        return new Patient(userMapper.toDomain(entity.getUser()), entity.getBloodType());
    }
}

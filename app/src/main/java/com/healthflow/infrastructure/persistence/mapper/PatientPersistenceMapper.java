package com.healthflow.infrastructure.persistence.mapper;

import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.infrastructure.persistence.entity.PatientEntity;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        uses = UserPersistenceMapper.class
)
public abstract class PatientPersistenceMapper {

    private UserPersistenceMapper userMapper;

    @Autowired
    public void setUserMapper(UserPersistenceMapper userMapper) {
        this.userMapper = userMapper;
    }

    public abstract PatientEntity toEntity(Patient patient);

    public Patient toDomain(PatientEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Patient(
                userMapper.toDomain(entity.getUser()),
                entity.getBloodType()
        ); // blood type ile alakalı domain modelde constructor sorunu olduğu için yapay zeka kodu bu şekilde değiştirmemi önerdi. ambiguous constructor hatası oldu
    }
}
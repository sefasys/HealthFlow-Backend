package com.healthflow.infrastructure.persistence.mapper;

import com.healthflow.domain.model.user.staff.Staff;
import com.healthflow.infrastructure.persistence.entity.StaffEntity;
import com.healthflow.infrastructure.persistence.entity.StaffType;
import com.healthflow.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class StaffPersistenceMapper {

    private UserPersistenceMapper userMapper;

    public StaffPersistenceMapper(UserPersistenceMapper userMapper) {
        this.userMapper = userMapper;
    }

    public StaffEntity toEntity(Staff staff, UserEntity userEntity, StaffType staffType){
        return new StaffEntity(staffType, staff.getEmploymentStatus(), staff.getHireDate(), staff.getEmployeeId(), userEntity);
    }

    public Staff toDomain(StaffEntity entity){
        return new Staff(entity.getEmployeeId(), entity.getHireDate(), entity.getEmploymentStatus(), userMapper.toDomain(entity.getUser()));
    }

}

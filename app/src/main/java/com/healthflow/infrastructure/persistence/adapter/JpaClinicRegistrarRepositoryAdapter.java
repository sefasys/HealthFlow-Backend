package com.healthflow.infrastructure.persistence.adapter;

import com.healthflow.application.exception.ClinicRegistrarNotFoundException;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.staff.ClinicRegistrar;
import com.healthflow.infrastructure.persistence.entity.StaffEntity;
import com.healthflow.infrastructure.persistence.entity.StaffType;
import com.healthflow.infrastructure.persistence.entity.UserEntity;
import com.healthflow.infrastructure.persistence.mapper.StaffPersistenceMapper;
import com.healthflow.infrastructure.persistence.repository.SpringDataStaffRepository;
import com.healthflow.infrastructure.persistence.repository.SpringDataUserRepository;
import com.healthflow.port.repository.IClinicRegistrarRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaClinicRegistrarRepositoryAdapter implements IClinicRegistrarRepository {

    private static final StaffType type = StaffType.CLINIC_REGISTRAR;

    private final SpringDataUserRepository userRepository;
    private final SpringDataStaffRepository staffRepository;
    private final StaffPersistenceMapper mapper;


    public JpaClinicRegistrarRepositoryAdapter(SpringDataUserRepository userRepository, SpringDataStaffRepository staffRepository, StaffPersistenceMapper mapper) {
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.mapper = mapper;
    }

    public List<ClinicRegistrar> getClinicRegistrars(){
        return staffRepository.findAllByStaffType(type).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public void addClinicRegistrar(ClinicRegistrar clinicRegistrar) {
        UserEntity user = userRepository.getReferenceById(clinicRegistrar.getStaff().getUserId());
        staffRepository.save(mapper.toEntity(clinicRegistrar.getStaff(), user, type));
    }

    @Override
    public Optional<ClinicRegistrar> findByUniqueId(UUID uniqueId) {
        return staffRepository.findByUserIdAndStaffType(uniqueId, type).map(this::toDomain);
    }

    @Override
    public Optional<ClinicRegistrar> findByNationalId(NationalId nationalId) {
        return staffRepository.findByNationalIdAndStaffType(nationalId.getValue(), type)
                .map(this::toDomain);
    }

    @Override
    public List<ClinicRegistrar> search(String query) {
        return staffRepository.search(query, type).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public void update(ClinicRegistrar clinicRegistrar) {
        StaffEntity entity = staffRepository
                .findByUserIdAndStaffType(clinicRegistrar.getStaff().getUserId(), type)
                .orElseThrow(() -> new ClinicRegistrarNotFoundException("Clinic registrar could not be found."));
        entity.changeEmploymentStatus(clinicRegistrar.getStaff().getEmploymentStatus());
    }



    private ClinicRegistrar toDomain(StaffEntity entity) {
        return new ClinicRegistrar(mapper.toDomain(entity));
    }
}
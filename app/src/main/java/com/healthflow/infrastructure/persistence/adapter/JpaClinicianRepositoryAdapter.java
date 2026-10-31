package com.healthflow.infrastructure.persistence.adapter;

import com.healthflow.application.exception.ClinicianNotFoundException;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.infrastructure.persistence.entity.StaffEntity;
import com.healthflow.infrastructure.persistence.entity.StaffType;
import com.healthflow.infrastructure.persistence.entity.UserEntity;
import com.healthflow.infrastructure.persistence.mapper.StaffPersistenceMapper;
import com.healthflow.infrastructure.persistence.repository.SpringDataStaffRepository;
import com.healthflow.infrastructure.persistence.repository.SpringDataUserRepository;
import com.healthflow.port.repository.IClinicianRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaClinicianRepositoryAdapter implements IClinicianRepository {

    private static final StaffType type = StaffType.CLINICIAN;

    private final SpringDataUserRepository userRepository;
    private final SpringDataStaffRepository staffRepository;
    private final StaffPersistenceMapper mapper;


    public JpaClinicianRepositoryAdapter(SpringDataUserRepository userRepository, SpringDataStaffRepository staffRepository, StaffPersistenceMapper mapper) {
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.mapper = mapper;
    }

    public List<Clinician> getClinicians(){
        return staffRepository.findAllByStaffType(type).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public void addClinician(Clinician clinician) {
        UserEntity user = userRepository.getReferenceById(clinician.getStaff().getUserId());
        staffRepository.save(mapper.toEntity(clinician.getStaff(), user, type));
    }

    @Override
    public Optional<Clinician> findByUniqueId(UUID uniqueId) {
        return staffRepository.findByUserIdAndStaffType(uniqueId, type).map(this::toDomain);
    }

    @Override
    public Optional<Clinician> findByNationalId(NationalId nationalId) {
        return staffRepository.findByNationalIdAndStaffType(nationalId.getValue(), type)
                .map(this::toDomain);
    }

    @Override
    public List<Clinician> search(String query) {
        return staffRepository.search(query, type).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public void update(Clinician clinician) {
        StaffEntity entity = staffRepository
                .findByUserIdAndStaffType(clinician.getStaff().getUserId(), type)
                .orElseThrow(() -> new ClinicianNotFoundException("Clinician could not be found."));
        entity.changeEmploymentStatus(clinician.getStaff().getEmploymentStatus());
    }



    private Clinician toDomain(StaffEntity entity) {
        return new Clinician(mapper.toDomain(entity));
    }
}

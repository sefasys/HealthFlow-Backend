package com.healthflow.infrastructure.persistence.adapter;

import com.healthflow.domain.model.appointment.Availability;
import com.healthflow.domain.model.appointment.AvailabilityStatus;
import com.healthflow.infrastructure.persistence.entity.StaffEntity;
import com.healthflow.infrastructure.persistence.mapper.AvailabilityPersistenceMapper;
import com.healthflow.infrastructure.persistence.repository.SpringDataAvailabilityRepository;
import com.healthflow.infrastructure.persistence.repository.SpringDataStaffRepository;
import com.healthflow.port.repository.IAvailabilityRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAvailabilityRepositoryAdapter implements IAvailabilityRepository {

    private final SpringDataAvailabilityRepository availabilityRepository;
    private final SpringDataStaffRepository staffRepository;
    private final AvailabilityPersistenceMapper mapper;

    public JpaAvailabilityRepositoryAdapter(SpringDataAvailabilityRepository availabilityRepository, SpringDataStaffRepository staffRepository, AvailabilityPersistenceMapper mapper) {
        this.availabilityRepository = availabilityRepository;
        this.staffRepository = staffRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Availability save(Availability availability) {
        availabilityRepository.findById(availability.getUniqueId()).ifPresentOrElse(
                entity -> entity.applyReview(
                        availability.getStatus(),
                        availability.getReviewedByUserId(),
                        availability.getReviewedAt(),
                        availability.getRejectionReason()),
                () -> {
                    StaffEntity clinician = staffRepository.getReferenceById(
                            availability.getClinician().getStaff().getUserId());
                    availabilityRepository.save(mapper.toEntity(availability, clinician));
                });
        return availability;
    }

    @Override
    public List<Availability> getAvailabilities() {
        return availabilityRepository.findAllWithClinician().stream().map(mapper::toDomain).toList();    }

    @Override
    public Optional<Availability> findById(UUID availabilityId) {
        return availabilityRepository.findWithClinicianById(availabilityId).map(mapper::toDomain);    }

    @Override
    public List<Availability> findByClinicianId(UUID clinicianId) {
        return availabilityRepository.findByClinicianId(clinicianId).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<Availability> findByClinicianIdAndDate(UUID clinicianId, LocalDate date) {
        return availabilityRepository.findByClinicianIdAndDate(clinicianId, date).stream()
                .map(mapper::toDomain).toList();    }

    @Override
    public List<Availability> findByStatus(AvailabilityStatus availabilityStatus) {
        return availabilityRepository.findByStatus(availabilityStatus).stream()
                .map(mapper::toDomain).toList();    }
}

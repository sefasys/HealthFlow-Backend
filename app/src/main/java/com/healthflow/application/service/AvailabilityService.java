package com.healthflow.application.service;

import com.healthflow.application.dto.availability.AvailabilityResponseDto;
import com.healthflow.application.dto.availability.CreateAvailabilityRequestDto;
import com.healthflow.application.dto.availability.RejectAvailabilityRequestDto;
import com.healthflow.application.exception.AvailabilityConflictException;
import com.healthflow.application.exception.AvailabilityNotFoundException;
import com.healthflow.application.exception.ClinicRegistrarNotFoundException;
import com.healthflow.application.exception.ClinicianNotFoundException;
import com.healthflow.application.mapper.availability.AvailabilityMapper;
import com.healthflow.domain.exception.InvalidAvailabilityException;
import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.domain.model.appointment.Availability;
import com.healthflow.domain.model.appointment.AvailabilityStatus;
import com.healthflow.domain.model.appointment.TimeRange;
import com.healthflow.domain.model.user.staff.ClinicRegistrar;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.domain.model.user.staff.EmploymentStatus;
import com.healthflow.port.repository.IAvailabilityRepository;
import com.healthflow.port.repository.IClinicRegistrarRepository;
import com.healthflow.port.repository.IClinicianRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AvailabilityService {

    private final IAvailabilityRepository availabilityRepository;
    private final AvailabilityMapper availabilityMapper;
    private final Clock clock;
    private final IClinicianRepository clinicianRepository;
    private final IClinicRegistrarRepository clinicRegistrarRepository;

    public AvailabilityService(IAvailabilityRepository availabilityRepository, AvailabilityMapper availabilityMapper, Clock clock, IClinicianRepository clinicianRepository, IClinicRegistrarRepository clinicRegistrarRepository){
        this.availabilityRepository = availabilityRepository;
        this.availabilityMapper = availabilityMapper;
        this.clock = clock;
        this.clinicianRepository = clinicianRepository;
        this.clinicRegistrarRepository = clinicRegistrarRepository;
    }

    public AvailabilityResponseDto createAvailability(
            UUID clinicianId,
            CreateAvailabilityRequestDto request
    ) {
        // 1. Müsaitliği oluşturacak doktoru bul.
        Clinician clinician = clinicianRepository.findByUniqueId(clinicianId)
                .orElseThrow(() ->
                        new ClinicianNotFoundException("Clinician could not be found.")
                );
        if (clinician.getStaff().getEmploymentStatus()
                != EmploymentStatus.ACTIVE) {
            throw new InvalidAvailabilityException(
                    "Only active clinicians can create availability."
            );
        }

        // 2. İstenen zaman aralığını ve yeni availability'yi oluştur.
        TimeRange timeRange = new TimeRange(
                request.startTime(),
                request.endTime()
        );

        Availability availability = new Availability(
                UUID.randomUUID(),
                clinician,
                request.date(),
                timeRange
        );
        requireFutureStart(availability);

        // 3. Doktorun aynı günkü kayıtlarında çakışma ara.
        List<Availability> existingAvailabilities =
                availabilityRepository.findByClinicianIdAndDate(
                        clinicianId,
                        request.date()
                );

        boolean hasOverlap = existingAvailabilities.stream()
                .filter(existing ->
                        existing.getStatus() != AvailabilityStatus.REJECTED
                )
                .anyMatch(existing -> existing.overlaps(availability));

        if (hasOverlap) {
            throw new AvailabilityConflictException(
                    "There is a conflict in time ranges."
            );
        }

        // 4. Kaydet ve response DTO'ya dönüştür.
        Availability savedAvailability =
                availabilityRepository.save(availability);

        return availabilityMapper.responseDto(savedAvailability);
    }

    private void requireFutureStart(Availability availability) {
        LocalDateTime start = LocalDateTime.of(
                availability.getDate(),
                availability.getTimeRange().start()
        );

        if (!start.isAfter(LocalDateTime.now(clock))) {
            throw new InvalidAvailabilityException(
                    "Availability start must be in the future."
            );
        }
    }

    public List<AvailabilityResponseDto> getAvailabilities(){
        return availabilityRepository.getAvailabilities().stream().map(availabilityMapper::responseDto).toList();
    }

    public AvailabilityResponseDto getAvailabilityById(UUID uniqueId){
        if (uniqueId == null) {
            throw new InvalidUniqueIdException("Unique ID cannot be null.");
        }
        Availability availability = availabilityRepository
                .findById(uniqueId)
                .orElseThrow(() -> new AvailabilityNotFoundException("Availability not found with unique id: " + uniqueId));

        return availabilityMapper.responseDto(availability);
    }

    public List<AvailabilityResponseDto> getAvailabilitiesByClinicianId(UUID clinicianId){
        return availabilityRepository.findByClinicianId(clinicianId).stream().map(availabilityMapper::responseDto).toList();
    }

    public List<AvailabilityResponseDto> getAvailabilitiesByClinicianAndDate(UUID clinicianId, LocalDate date){
        return availabilityRepository.findByClinicianIdAndDate(clinicianId, date).stream().map(availabilityMapper::responseDto).toList();
    }

    public List<AvailabilityResponseDto> getPendingAvailabilities() {
        return availabilityRepository
                .findByStatus(AvailabilityStatus.PENDING_REVIEW)
                .stream()
                .map(availabilityMapper::responseDto)
                .toList();
    }

    public AvailabilityResponseDto publishAvailability(
            UUID availabilityId,
            UUID registrarUserId
    ) {
        Availability availability = availabilityRepository
                .findById(availabilityId)
                .orElseThrow(() ->
                        new AvailabilityNotFoundException(
                                "Müsaitlik kaydı bulunamadı."
                        )
                );

        ClinicRegistrar registrar = clinicRegistrarRepository
                .findByUniqueId(registrarUserId)
                .orElseThrow(() ->
                        new ClinicRegistrarNotFoundException(
                                "Klinik kayıt görevlisi bulunamadı."
                        )
                );

        requireFutureStart(availability);
        availability.publish(registrar, Instant.now());

        Availability savedAvailability =
                availabilityRepository.save(availability);

        return availabilityMapper.responseDto(savedAvailability);
    }

    public AvailabilityResponseDto rejectAvailability(
            UUID availabilityId,
            UUID registrarUserId,
            RejectAvailabilityRequestDto request
    ) {
        Availability availability = availabilityRepository
                .findById(availabilityId)
                .orElseThrow(() ->
                        new AvailabilityNotFoundException(
                                "Müsaitlik kaydı bulunamadı."
                        )
                );

        ClinicRegistrar registrar = clinicRegistrarRepository
                .findByUniqueId(registrarUserId)
                .orElseThrow(() ->
                        new ClinicRegistrarNotFoundException(
                                "Klinik kayıt görevlisi bulunamadı."
                        )
                );

        availability.reject(
                registrar,
                Instant.now(clock),
                request.reason()
        );

        Availability savedAvailability =
                availabilityRepository.save(availability);

        return availabilityMapper.responseDto(savedAvailability);
    }
}


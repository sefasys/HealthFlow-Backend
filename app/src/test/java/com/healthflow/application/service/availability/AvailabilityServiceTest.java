package com.healthflow.application.service.availability;

import com.healthflow.application.dto.availability.*;
import com.healthflow.application.exception.AvailabilityConflictException;
import com.healthflow.application.exception.ClinicRegistrarNotFoundException;
import com.healthflow.application.exception.ClinicianNotFoundException;
import com.healthflow.application.mapper.availability.AvailabilityMapper;
import com.healthflow.application.service.AvailabilityService;
import com.healthflow.domain.exception.InvalidAvailabilityException;
import com.healthflow.domain.factory.StaffFactory;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.appointment.Availability;
import com.healthflow.domain.model.appointment.AvailabilityStatus;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.staff.ClinicRegistrar;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.domain.model.user.staff.EmploymentStatus;
import com.healthflow.infrastructure.repository.InMemoryAvailabilityRepository;
import com.healthflow.infrastructure.repository.InMemoryClinicRegistrarRepository;
import com.healthflow.infrastructure.repository.InMemoryClinicianRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.*;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityServiceTest {

    private static final ZoneId ZONE = ZoneId.of("Europe/Istanbul");
    private static final Instant NOW =
            Instant.parse("2026-10-10T09:00:00Z");

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 10);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    private InMemoryAvailabilityRepository availabilityRepository;
    private InMemoryClinicianRepository clinicianRepository;
    private InMemoryClinicRegistrarRepository registrarRepository;
    private AvailabilityService service;

    private UUID clinicianId;
    private UUID registrarId;

    @BeforeEach
    void setUp() {
        availabilityRepository = new InMemoryAvailabilityRepository();
        clinicianRepository = new InMemoryClinicianRepository();
        registrarRepository = new InMemoryClinicRegistrarRepository();

        service = serviceWithClock(Clock.fixed(NOW, ZONE));

        clinicianId = saveClinician(
                "10000000146",
                EmploymentStatus.ACTIVE
        );

        registrarId = saveRegistrar(EmploymentStatus.ACTIVE);
    }

    @Test
    void createAvailability_shouldSavePendingAvailability() {
        AvailabilityResponseDto response = createTomorrow(9, 10);

        Availability saved = savedAvailability(response.uniqueId());

        assertAll(
                () -> assertNotNull(response.uniqueId()),
                () -> assertEquals(clinicianId, response.clinicianId()),
                () -> assertEquals(TOMORROW, response.date()),
                () -> assertEquals(LocalTime.of(9, 0), response.startTime()),
                () -> assertEquals(LocalTime.of(10, 0), response.endTime()),
                () -> assertEquals(
                        AvailabilityStatus.PENDING_REVIEW,
                        response.status()
                ),
                () -> assertEquals(
                        AvailabilityStatus.PENDING_REVIEW,
                        saved.getStatus()
                ),
                () -> assertNull(saved.getReviewedByUserId()),
                () -> assertNull(saved.getReviewedAt()),
                () -> assertNull(response.rejectionReason()),
                () -> assertEquals(
                        1,
                        availabilityRepository.getAvailabilities().size()
                )
        );
    }

    @Test
    void createAvailability_shouldRejectUnknownClinician() {
        assertThrows(
                ClinicianNotFoundException.class,
                () -> service.createAvailability(
                        UUID.randomUUID(),
                        request(TOMORROW, 9, 10)
                )
        );

        assertTrue(availabilityRepository.getAvailabilities().isEmpty());
    }

    @Test
    void createAvailability_shouldRejectInactiveClinician() {
        UUID inactiveId = saveClinician(
                "10000000154",
                EmploymentStatus.INACTIVE
        );

        assertThrows(
                InvalidAvailabilityException.class,
                () -> service.createAvailability(
                        inactiveId,
                        request(TOMORROW, 9, 10)
                )
        );

        assertTrue(availabilityRepository.getAvailabilities().isEmpty());
    }

    @Test
    void createAvailability_shouldRejectPastStartToday() {
        // Türkiye saati 12.00; 11.00'de başlayan aralık geçmişte.
        assertThrows(
                InvalidAvailabilityException.class,
                () -> service.createAvailability(
                        clinicianId,
                        request(TODAY, 11, 13)
                )
        );
    }

    @Test
    void createAvailability_shouldRejectStartExactlyNow() {
        assertThrows(
                InvalidAvailabilityException.class,
                () -> service.createAvailability(
                        clinicianId,
                        request(TODAY, 12, 13)
                )
        );
    }

    @Test
    void createAvailability_shouldRejectOverlappingPendingAvailability() {
        createTomorrow(9, 11);

        assertThrows(
                AvailabilityConflictException.class,
                () -> createTomorrow(10, 12)
        );

        assertEquals(1, availabilityRepository.getAvailabilities().size());
    }

    @Test
    void createAvailability_shouldRejectOverlappingPublishedAvailability() {
        AvailabilityResponseDto existing = createTomorrow(9, 11);
        service.publishAvailability(existing.uniqueId(), registrarId);

        assertThrows(
                AvailabilityConflictException.class,
                () -> createTomorrow(10, 12)
        );

        assertEquals(1, availabilityRepository.getAvailabilities().size());
    }

    @Test
    void createAvailability_shouldAllowAdjacentTimeRanges() {
        createTomorrow(9, 10);

        AvailabilityResponseDto second = createTomorrow(10, 11);

        assertAll(
                () -> assertEquals(LocalTime.of(10, 0), second.startTime()),
                () -> assertEquals(
                        2,
                        availabilityRepository.getAvailabilities().size()
                )
        );
    }

    @Test
    void createAvailability_shouldAllowSameTimeForDifferentClinicians() {
        createTomorrow(9, 10);

        UUID otherClinicianId = saveClinician(
                "10000000154",
                EmploymentStatus.ACTIVE
        );

        AvailabilityResponseDto second = service.createAvailability(
                otherClinicianId,
                request(TOMORROW, 9, 10)
        );

        assertAll(
                () -> assertEquals(otherClinicianId, second.clinicianId()),
                () -> assertEquals(
                        2,
                        availabilityRepository.getAvailabilities().size()
                )
        );
    }

    @Test
    void createAvailability_shouldAllowReuseOfRejectedTimeRange() {
        AvailabilityResponseDto first = createTomorrow(9, 10);

        service.rejectAvailability(
                first.uniqueId(),
                registrarId,
                new RejectAvailabilityRequestDto("Schedule needs revision.")
        );

        AvailabilityResponseDto second = createTomorrow(9, 10);

        assertAll(
                () -> assertNotEquals(first.uniqueId(), second.uniqueId()),
                () -> assertEquals(
                        AvailabilityStatus.PENDING_REVIEW,
                        second.status()
                ),
                () -> assertEquals(
                        AvailabilityStatus.REJECTED,
                        savedAvailability(first.uniqueId()).getStatus()
                )
        );
    }

    @Test
    void createAvailability_shouldRejectDurationNotDivisibleBy15Minutes() {
        assertThrows(
                InvalidAvailabilityException.class,
                () -> service.createAvailability(
                        clinicianId,
                        new CreateAvailabilityRequestDto(
                                TOMORROW,
                                LocalTime.of(9, 0),
                                LocalTime.of(9, 20)
                        )
                )
        );

        assertTrue(availabilityRepository.getAvailabilities().isEmpty());
    }

    @Test
    void publishAvailability_shouldSavePublishedStatusAndReviewMetadata() {
        AvailabilityResponseDto created = createTomorrow(9, 10);

        AvailabilityResponseDto response = service.publishAvailability(
                created.uniqueId(),
                registrarId
        );

        Availability saved = savedAvailability(created.uniqueId());

        assertAll(
                () -> assertEquals(created.uniqueId(), response.uniqueId()),
                () -> assertEquals(
                        AvailabilityStatus.PUBLISHED,
                        response.status()
                ),
                () -> assertEquals(
                        AvailabilityStatus.PUBLISHED,
                        saved.getStatus()
                ),
                () -> assertEquals(registrarId, saved.getReviewedByUserId()),
                () -> assertEquals(NOW, saved.getReviewedAt()),
                () -> assertNull(saved.getRejectionReason())
        );
    }

    @Test
    void rejectAvailability_shouldSaveRejectedStatusAndTrimmedReason() {
        AvailabilityResponseDto created = createTomorrow(9, 10);

        AvailabilityResponseDto response = service.rejectAvailability(
                created.uniqueId(),
                registrarId,
                new RejectAvailabilityRequestDto("  Schedule conflict.  ")
        );

        Availability saved = savedAvailability(created.uniqueId());

        assertAll(
                () -> assertEquals(
                        AvailabilityStatus.REJECTED,
                        response.status()
                ),
                () -> assertEquals(
                        AvailabilityStatus.REJECTED,
                        saved.getStatus()
                ),
                () -> assertEquals("Schedule conflict.", response.rejectionReason()),
                () -> assertEquals("Schedule conflict.", saved.getRejectionReason()),
                () -> assertEquals(registrarId, saved.getReviewedByUserId()),
                () -> assertEquals(NOW, saved.getReviewedAt())
        );
    }

    @Test
    void rejectAvailability_shouldRejectBlankReasonWithoutChangingState() {
        AvailabilityResponseDto created = createTomorrow(9, 10);

        assertThrows(
                InvalidAvailabilityException.class,
                () -> service.rejectAvailability(
                        created.uniqueId(),
                        registrarId,
                        new RejectAvailabilityRequestDto("   ")
                )
        );

        Availability saved = savedAvailability(created.uniqueId());

        assertAll(
                () -> assertEquals(
                        AvailabilityStatus.PENDING_REVIEW,
                        saved.getStatus()
                ),
                () -> assertNull(saved.getReviewedByUserId()),
                () -> assertNull(saved.getReviewedAt()),
                () -> assertNull(saved.getRejectionReason())
        );
    }

    @Test
    void publishAvailability_shouldRejectAlreadyPublishedAvailability() {
        AvailabilityResponseDto created = createTomorrow(9, 10);
        service.publishAvailability(created.uniqueId(), registrarId);

        assertThrows(
                InvalidAvailabilityException.class,
                () -> service.publishAvailability(
                        created.uniqueId(),
                        registrarId
                )
        );

        assertEquals(
                AvailabilityStatus.PUBLISHED,
                savedAvailability(created.uniqueId()).getStatus()
        );
    }

    @Test
    void publishAvailability_shouldRejectRejectedAvailability() {
        AvailabilityResponseDto created = createTomorrow(9, 10);

        service.rejectAvailability(
                created.uniqueId(),
                registrarId,
                new RejectAvailabilityRequestDto("Schedule conflict.")
        );

        assertThrows(
                InvalidAvailabilityException.class,
                () -> service.publishAvailability(
                        created.uniqueId(),
                        registrarId
                )
        );

        assertEquals(
                AvailabilityStatus.REJECTED,
                savedAvailability(created.uniqueId()).getStatus()
        );
    }

    @Test
    void publishAvailability_shouldRejectInactiveRegistrar() {
        AvailabilityResponseDto created = createTomorrow(9, 10);
        UUID inactiveRegistrarId = saveRegistrar(EmploymentStatus.INACTIVE);

        assertThrows(
                InvalidAvailabilityException.class,
                () -> service.publishAvailability(
                        created.uniqueId(),
                        inactiveRegistrarId
                )
        );

        assertEquals(
                AvailabilityStatus.PENDING_REVIEW,
                savedAvailability(created.uniqueId()).getStatus()
        );
    }

    @Test
    void publishAvailability_shouldRejectAvailabilityWhoseStartHasPassed() {
        AvailabilityResponseDto created = createTomorrow(9, 10);

        // Ertesi gün Türkiye saati 09.30: müsaitliğin başlangıcı geçti.
        Clock laterClock = Clock.fixed(
                Instant.parse("2026-10-11T06:30:00Z"),
                ZONE
        );

        AvailabilityService laterService = serviceWithClock(laterClock);

        assertThrows(
                InvalidAvailabilityException.class,
                () -> laterService.publishAvailability(
                        created.uniqueId(),
                        registrarId
                )
        );

        assertEquals(
                AvailabilityStatus.PENDING_REVIEW,
                savedAvailability(created.uniqueId()).getStatus()
        );
    }

    @Test
    void publishAvailability_shouldRejectUnknownRegistrar() {
        AvailabilityResponseDto created = createTomorrow(9, 10);

        assertThrows(
                ClinicRegistrarNotFoundException.class,
                () -> service.publishAvailability(
                        created.uniqueId(),
                        UUID.randomUUID()
                )
        );

        assertEquals(
                AvailabilityStatus.PENDING_REVIEW,
                savedAvailability(created.uniqueId()).getStatus()
        );
    }

    @Test
    void getPendingAvailabilities_shouldReturnOnlyPendingRecords() {
        AvailabilityResponseDto pending = createTomorrow(9, 10);
        AvailabilityResponseDto published = createTomorrow(10, 11);
        AvailabilityResponseDto rejected = createTomorrow(11, 12);

        service.publishAvailability(published.uniqueId(), registrarId);

        service.rejectAvailability(
                rejected.uniqueId(),
                registrarId,
                new RejectAvailabilityRequestDto("Schedule conflict.")
        );

        List<AvailabilityResponseDto> result =
                service.getPendingAvailabilities();

        assertEquals(List.of(pending), result);
    }

    private AvailabilityService serviceWithClock(Clock clock) {
        return new AvailabilityService(
                availabilityRepository,
                Mappers.getMapper(AvailabilityMapper.class),
                clock,
                clinicianRepository,
                registrarRepository
        );
    }

    private AvailabilityResponseDto createTomorrow(int startHour, int endHour) {
        return service.createAvailability(
                clinicianId,
                request(TOMORROW, startHour, endHour)
        );
    }

    private CreateAvailabilityRequestDto request(
            LocalDate date,
            int startHour,
            int endHour
    ) {
        return new CreateAvailabilityRequestDto(
                date,
                LocalTime.of(startHour, 0),
                LocalTime.of(endHour, 0)
        );
    }

    private Availability savedAvailability(UUID id) {
        return availabilityRepository.findById(id).orElseThrow();
    }

    private UUID saveClinician(String nationalId, EmploymentStatus status) {
        User user = createUser(nationalId, UserRole.CLINICIAN);

        Clinician clinician = new Clinician(
                new StaffFactory().createStaff(
                        LocalDate.of(2024, 1, 15),
                        status,
                        user
                )
        );

        clinicianRepository.addClinician(clinician);
        return user.getUniqueId();
    }

    private UUID saveRegistrar(EmploymentStatus status) {
        String nationalId = status == EmploymentStatus.ACTIVE
                ? "10000000162"
                : "10000000170";

        User user = createUser(nationalId, UserRole.CLINIC_REGISTRAR);

        ClinicRegistrar registrar = new ClinicRegistrar(
                new StaffFactory().createStaff(
                        LocalDate.of(2024, 1, 15),
                        status,
                        user
                )
        );

        registrarRepository.addClinicRegistrar(registrar);
        return user.getUniqueId();
    }

    private User createUser(String nationalId, UserRole role) {
        return new UserFactory().createUser(
                new NationalId(nationalId),
                "Deniz",
                "Yilmaz",
                LocalDate.of(1990, 5, 10),
                "deniz@example.com",
                "05000000000",
                role
        );
    }
}
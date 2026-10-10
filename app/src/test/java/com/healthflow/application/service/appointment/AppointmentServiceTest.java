package com.healthflow.application.service.appointment;

import com.healthflow.application.dto.appointment.*;
import com.healthflow.application.exception.*;
import com.healthflow.application.mapper.appointment.AppointmentMapper;
import com.healthflow.application.service.AppointmentService;
import com.healthflow.domain.exception.AppointmentConflictException;
import com.healthflow.domain.exception.InvalidAppointmentException;
import com.healthflow.domain.factory.StaffFactory;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.appointment.*;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.model.user.staff.ClinicRegistrar;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.domain.model.user.staff.EmploymentStatus;
import com.healthflow.domain.service.AppointmentScheduler;
import com.healthflow.domain.service.SlotGenerator;
import com.healthflow.infrastructure.repository.InMemoryAppointmentRepository;
import com.healthflow.infrastructure.repository.InMemoryAvailabilityRepository;
import com.healthflow.infrastructure.repository.InMemoryPatientRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.*;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentServiceTest {

    private static final ZoneId ZONE = ZoneId.of("Europe/Istanbul");

    // Türkiye saatiyle 10 Ekim 2026, 12.00.
    private static final Instant NOW =
            Instant.parse("2026-10-10T09:00:00Z");

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 10);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    private InMemoryAppointmentRepository appointmentRepository;
    private InMemoryAvailabilityRepository availabilityRepository;
    private InMemoryPatientRepository patientRepository;
    private AppointmentService service;

    private Patient patient;
    private Patient otherPatient;
    private Clinician clinician;
    private Clinician otherClinician;
    private ClinicRegistrar registrar;
    private Availability availability;

    @BeforeEach
    void setUp() {
        appointmentRepository = new InMemoryAppointmentRepository();
        availabilityRepository = new InMemoryAvailabilityRepository();
        patientRepository = new InMemoryPatientRepository();

        Clock clock = Clock.fixed(NOW, ZONE);

        service = new AppointmentService(
                appointmentRepository,
                availabilityRepository,
                clock,
                patientRepository,
                Mappers.getMapper(AppointmentMapper.class),
                new AppointmentScheduler(clock),
                new SlotGenerator()
        );

        patient = savePatient(createUser(
                "10000000146", UserRole.PATIENT
        ));

        otherPatient = savePatient(createUser(
                "10000000154", UserRole.PATIENT
        ));

        clinician = createClinician(createUser(
                "10000000162", UserRole.CLINICIAN
        ));

        otherClinician = createClinician(createUser(
                "10000000170", UserRole.CLINICIAN
        ));

        registrar = new ClinicRegistrar(
                new StaffFactory().createStaff(
                        LocalDate.of(2024, 1, 15),
                        EmploymentStatus.ACTIVE,
                        createUser("10000000188", UserRole.CLINIC_REGISTRAR)
                )
        );

        availability = savePublishedAvailability(clinician, TOMORROW);
    }

    @Test
    void bookAppointment_shouldSaveScheduledAppointment() {
        AppointmentResponseDto response =
                book(patient, availability, LocalTime.of(9, 0));

        Appointment saved = savedAppointment(response.uniqueId());

        assertAll(
                () -> assertNotNull(response.uniqueId()),
                () -> assertEquals(
                        availability.getUniqueId(), response.availabilityId()
                ),
                () -> assertEquals(patientId(patient), response.patientId()),
                () -> assertEquals(clinicianId(clinician), response.clinicianId()),
                () -> assertEquals(TOMORROW, response.date()),
                () -> assertEquals(LocalTime.of(9, 0), response.startTime()),
                () -> assertEquals(LocalTime.of(9, 15), response.endTime()),
                () -> assertEquals(AppointmentStatus.SCHEDULED, response.status()),
                () -> assertEquals(AppointmentStatus.SCHEDULED, saved.getStatus()),
                () -> assertSame(patient, saved.getPatient()),
                () -> assertSame(clinician, saved.getClinician())
        );
    }

    @Test
    void bookAppointment_shouldRejectUnknownPatient() {
        assertThrows(
                PatientNotFoundException.class,
                () -> service.bookAppointment(
                        UUID.randomUUID(),
                        new BookAppointmentRequestDto(
                                availability.getUniqueId(),
                                LocalTime.of(9, 0)
                        )
                )
        );
    }

    @Test
    void bookAppointment_shouldRejectUnknownAvailability() {
        assertThrows(
                AvailabilityNotFoundException.class,
                () -> service.bookAppointment(
                        patientId(patient),
                        new BookAppointmentRequestDto(
                                UUID.randomUUID(),
                                LocalTime.of(9, 0)
                        )
                )
        );
    }

    @Test
    void bookAppointment_shouldRejectUnpublishedAvailability() {
        Availability pending = savePendingAvailability(clinician, TOMORROW);

        assertThrows(
                InvalidAppointmentException.class,
                () -> book(patient, pending, LocalTime.of(9, 0))
        );

        assertTrue(
                appointmentRepository.findByPatientId(patientId(patient)).isEmpty()
        );
    }

    @Test
    void bookAppointment_shouldRejectTimeOutsideAvailability() {
        // Müsaitlik 09.00–10.00; 10.00'da başlayan slot dışarıda kalır.
        assertThrows(
                InvalidAppointmentException.class,
                () -> book(patient, availability, LocalTime.of(10, 0))
        );
    }

    @Test
    void bookAppointment_shouldRejectStartNotAlignedWithSlot() {
        // 09.05–09.20 aralığı 15 dakika olsa da slot başlangıcına uymaz.
        assertThrows(
                InvalidAppointmentException.class,
                () -> book(patient, availability, LocalTime.of(9, 5))
        );
    }

    @Test
    void bookAppointment_shouldRejectPastSlot() {
        Availability past = savePublishedAvailability(clinician, TODAY);

        assertThrows(
                InvalidAppointmentException.class,
                () -> book(patient, past, LocalTime.of(9, 0))
        );

        assertTrue(
                appointmentRepository.findByPatientId(patientId(patient)).isEmpty()
        );
    }

    @Test
    void bookAppointment_shouldRejectClinicianBookingWithThemselves() {
        Patient clinicianAsPatient =
                savePatient(clinician.getStaff().getUser());

        assertThrows(
                InvalidAppointmentException.class,
                () -> book(
                        clinicianAsPatient,
                        availability,
                        LocalTime.of(9, 0)
                )
        );
    }

    @Test
    void bookAppointment_shouldRejectAlreadyBookedSlot() {
        book(patient, availability, LocalTime.of(9, 0));

        assertThrows(
                AppointmentConflictException.class,
                () -> book(otherPatient, availability, LocalTime.of(9, 0))
        );

        assertAll(
                () -> assertEquals(
                        1,
                        appointmentRepository.findByClinicianIdAndDate(
                                clinicianId(clinician), TOMORROW
                        ).size()
                ),
                () -> assertTrue(
                        appointmentRepository.findByPatientId(
                                patientId(otherPatient)
                        ).isEmpty()
                )
        );
    }

    @Test
    void bookAppointment_shouldRejectPatientOverlapWithAnotherClinician() {
        book(patient, availability, LocalTime.of(9, 0));

        Availability otherAvailability =
                savePublishedAvailability(otherClinician, TOMORROW);

        assertThrows(
                AppointmentConflictException.class,
                () -> book(patient, otherAvailability, LocalTime.of(9, 0))
        );

        assertEquals(
                1,
                appointmentRepository.findByPatientId(patientId(patient)).size()
        );
    }

    @Test
    void bookAppointment_shouldRejectWhenClinicianIsBusyAsPatient() {
        Patient clinicianAsPatient =
                savePatient(clinician.getStaff().getUser());

        Availability otherAvailability =
                savePublishedAvailability(otherClinician, TOMORROW);

        // Bizim doktorumuz, başka doktorun hastası olarak randevu aldı.
        book(clinicianAsPatient, otherAvailability, LocalTime.of(9, 0));

        // Aynı saatte kendi hastasını kabul edemez.
        assertThrows(
                AppointmentConflictException.class,
                () -> book(patient, availability, LocalTime.of(9, 0))
        );
    }

    @Test
    void bookAppointment_shouldRejectWhenPatientIsBusyAsClinician() {
        Clinician patientAsClinician = createClinician(patient.getUser());

        Availability patientWorkAvailability =
                savePublishedAvailability(patientAsClinician, TOMORROW);

        // Hastamızın doktor rolünde baktığı bir randevu var.
        book(otherPatient, patientWorkAvailability, LocalTime.of(9, 0));

        // Aynı saatte başka bir doktordan hasta olarak randevu alamaz.
        assertThrows(
                AppointmentConflictException.class,
                () -> book(patient, availability, LocalTime.of(9, 0))
        );
    }

    @Test
    void bookAppointment_shouldAllowAdjacentAppointments() {
        book(patient, availability, LocalTime.of(9, 0));

        AppointmentResponseDto second =
                book(otherPatient, availability, LocalTime.of(9, 15));

        assertAll(
                () -> assertEquals(LocalTime.of(9, 15), second.startTime()),
                () -> assertEquals(
                        2,
                        appointmentRepository.findByClinicianIdAndDate(
                                clinicianId(clinician), TOMORROW
                        ).size()
                )
        );
    }

    @Test
    void cancelAppointment_shouldCancelWithoutDeletingRecord() {
        AppointmentResponseDto created =
                book(patient, availability, LocalTime.of(9, 0));

        AppointmentResponseDto response = service.cancelAppointment(
                created.uniqueId(),
                patientId(patient)
        );

        assertAll(
                () -> assertEquals(created.uniqueId(), response.uniqueId()),
                () -> assertEquals(AppointmentStatus.CANCELLED, response.status()),
                () -> assertEquals(
                        AppointmentStatus.CANCELLED,
                        savedAppointment(created.uniqueId()).getStatus()
                ),
                () -> assertEquals(
                        1,
                        appointmentRepository.findByPatientId(
                                patientId(patient)
                        ).size()
                )
        );
    }

    @Test
    void cancelAppointment_shouldRejectAnotherPatient() {
        AppointmentResponseDto created =
                book(patient, availability, LocalTime.of(9, 0));

        assertThrows(
                AppointmentAccessDeniedException.class,
                () -> service.cancelAppointment(
                        created.uniqueId(),
                        patientId(otherPatient)
                )
        );

        assertEquals(
                AppointmentStatus.SCHEDULED,
                savedAppointment(created.uniqueId()).getStatus()
        );
    }

    @Test
    void bookAppointment_shouldAllowBookingCancelledSlotAgain() {
        AppointmentResponseDto first =
                book(patient, availability, LocalTime.of(9, 0));

        service.cancelAppointment(first.uniqueId(), patientId(patient));

        AppointmentResponseDto second =
                book(otherPatient, availability, LocalTime.of(9, 0));

        assertAll(
                () -> assertNotEquals(first.uniqueId(), second.uniqueId()),
                () -> assertEquals(AppointmentStatus.SCHEDULED, second.status()),
                () -> assertEquals(
                        AppointmentStatus.CANCELLED,
                        savedAppointment(first.uniqueId()).getStatus()
                )
        );
    }

    @Test
    void getAppointmentById_shouldReturnAppointment() {
        AppointmentResponseDto created =
                book(patient, availability, LocalTime.of(9, 0));

        assertEquals(
                created,
                service.getAppointmentById(created.uniqueId())
        );
    }

    @Test
    void getAppointmentById_shouldRejectUnknownId() {
        assertThrows(
                AppointmentNotFoundException.class,
                () -> service.getAppointmentById(UUID.randomUUID())
        );
    }

    @Test
    void getAppointmentsByPatientId_shouldReturnOnlyRequestedPatient() {
        AppointmentResponseDto expected =
                book(patient, availability, LocalTime.of(9, 0));

        book(otherPatient, availability, LocalTime.of(9, 15));

        assertEquals(
                List.of(expected),
                service.getAppointmentsByPatientId(patientId(patient))
        );
    }

    @Test
    void getAppointmentsByClinicianAndDate_shouldFilterBothFields() {
        AppointmentResponseDto expected =
                book(patient, availability, LocalTime.of(9, 0));

        Availability anotherDay =
                savePublishedAvailability(clinician, TOMORROW.plusDays(1));

        book(patient, anotherDay, LocalTime.of(9, 0));

        Availability anotherClinician =
                savePublishedAvailability(otherClinician, TOMORROW);

        book(otherPatient, anotherClinician, LocalTime.of(9, 0));

        assertEquals(
                List.of(expected),
                service.getAppointmentsByClinicianAndDate(
                        clinicianId(clinician),
                        TOMORROW
                )
        );
    }

    @Test
    void getAvailableSlots_shouldReturnFour15MinuteSlots() {
        List<AppointmentSlotResponseDto> slots =
                service.getAvailableSlots(availability.getUniqueId());

        assertEquals(
                List.of(
                        new AppointmentSlotResponseDto(
                                availability.getUniqueId(), TOMORROW,
                                LocalTime.of(9, 0), LocalTime.of(9, 15)
                        ),
                        new AppointmentSlotResponseDto(
                                availability.getUniqueId(), TOMORROW,
                                LocalTime.of(9, 15), LocalTime.of(9, 30)
                        ),
                        new AppointmentSlotResponseDto(
                                availability.getUniqueId(), TOMORROW,
                                LocalTime.of(9, 30), LocalTime.of(9, 45)
                        ),
                        new AppointmentSlotResponseDto(
                                availability.getUniqueId(), TOMORROW,
                                LocalTime.of(9, 45), LocalTime.of(10, 0)
                        )
                ),
                slots
        );
    }

    @Test
    void getAvailableSlots_shouldHideBookedSlotAndRestoreItAfterCancellation() {
        AppointmentResponseDto booked =
                book(patient, availability, LocalTime.of(9, 0));

        List<LocalTime> beforeCancellation =
                slotStartTimes(availability);

        assertEquals(
                List.of(
                        LocalTime.of(9, 15),
                        LocalTime.of(9, 30),
                        LocalTime.of(9, 45)
                ),
                beforeCancellation
        );

        service.cancelAppointment(booked.uniqueId(), patientId(patient));

        assertEquals(
                List.of(
                        LocalTime.of(9, 0),
                        LocalTime.of(9, 15),
                        LocalTime.of(9, 30),
                        LocalTime.of(9, 45)
                ),
                slotStartTimes(availability)
        );
    }

    @Test
    void getAvailableSlots_shouldReturnEmptyForPendingAvailability() {
        Availability pending = savePendingAvailability(clinician, TOMORROW);

        assertTrue(
                service.getAvailableSlots(pending.getUniqueId()).isEmpty()
        );
    }

    @Test
    void getAvailableSlots_shouldExcludePastAndCurrentStartTimes() {
        Availability todayAvailability = new Availability(
                UUID.randomUUID(),
                clinician,
                TODAY,
                new TimeRange(LocalTime.of(11, 45), LocalTime.of(12, 45))
        );

        todayAvailability.publish(registrar, NOW);
        availabilityRepository.save(todayAvailability);

        // Saat tam 12.00: 11.45 ve 12.00 başlangıçları gösterilmemeli.
        assertEquals(
                List.of(LocalTime.of(12, 15), LocalTime.of(12, 30)),
                slotStartTimes(todayAvailability)
        );
    }

    @Test
    void getAvailableSlots_shouldHideClinicianPatientRoleConflict() {
        Patient clinicianAsPatient =
                savePatient(clinician.getStaff().getUser());

        Availability otherAvailability =
                savePublishedAvailability(otherClinician, TOMORROW);

        book(clinicianAsPatient, otherAvailability, LocalTime.of(9, 0));

        assertEquals(
                List.of(
                        LocalTime.of(9, 15),
                        LocalTime.of(9, 30),
                        LocalTime.of(9, 45)
                ),
                slotStartTimes(availability)
        );
    }

    private AppointmentResponseDto book(
            Patient selectedPatient,
            Availability selectedAvailability,
            LocalTime start
    ) {
        return service.bookAppointment(
                patientId(selectedPatient),
                new BookAppointmentRequestDto(
                        selectedAvailability.getUniqueId(),
                        start
                )
        );
    }

    private Appointment savedAppointment(UUID id) {
        return appointmentRepository.findById(id).orElseThrow();
    }

    private List<LocalTime> slotStartTimes(Availability selectedAvailability) {
        return service.getAvailableSlots(selectedAvailability.getUniqueId())
                .stream()
                .map(AppointmentSlotResponseDto::startTime)
                .toList();
    }

    private Availability savePendingAvailability(
            Clinician selectedClinician,
            LocalDate date
    ) {
        Availability result = new Availability(
                UUID.randomUUID(),
                selectedClinician,
                date,
                new TimeRange(LocalTime.of(9, 0), LocalTime.of(10, 0))
        );

        return availabilityRepository.save(result);
    }

    private Availability savePublishedAvailability(
            Clinician selectedClinician,
            LocalDate date
    ) {
        Availability result =
                savePendingAvailability(selectedClinician, date);

        result.publish(registrar, NOW);

        return availabilityRepository.save(result);
    }

    private Patient savePatient(User user) {
        user.addRole(UserRole.PATIENT);
        Patient result = new Patient(user);
        patientRepository.addPatient(result);
        return result;
    }

    private Clinician createClinician(User user) {
        user.addRole(UserRole.CLINICIAN);

        return new Clinician(
                new StaffFactory().createStaff(
                        LocalDate.of(2024, 1, 15),
                        EmploymentStatus.ACTIVE,
                        user
                )
        );
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

    private UUID patientId(Patient selectedPatient) {
        return selectedPatient.getUser().getUniqueId();
    }

    private UUID clinicianId(Clinician selectedClinician) {
        return selectedClinician.getStaff().getUser().getUniqueId();
    }
}
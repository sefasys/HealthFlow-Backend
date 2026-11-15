package com.healthflow.service;

import com.healthflow.IntegrationTestBase;
import com.healthflow.dto.appointment.AppointmentResponse;
import com.healthflow.dto.appointment.BookAppointmentRequest;
import com.healthflow.dto.appointment.OpenSlotsRequest;
import com.healthflow.dto.availability.CreateAvailabilityRequest;
import com.healthflow.dto.clinician.ClinicianRegistrationRequest;
import com.healthflow.dto.clinicregistrar.ClinicRegistrarRegistrationRequest;
import com.healthflow.dto.patient.PatientRegistrationRequest;
import com.healthflow.entity.appointment.AppointmentStatus;
import com.healthflow.entity.availability.SlotDuration;
import com.healthflow.entity.user.Gender;
import com.healthflow.entity.staff.Specialty;
import com.healthflow.exception.AppointmentConflictException;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AppointmentServiceIT extends IntegrationTestBase {

    @Autowired AppointmentService appointmentService;
    @Autowired AvailabilityService availabilityService;
    @Autowired ClinicianService clinicianService;
    @Autowired ClinicRegistrarService registrarService;
    @Autowired PatientService patientService;

    LocalDate tomorrow;
    UUID clinicianId;
    UUID registrarId;
    UUID patientId;
    UUID availabilityId;

    @BeforeEach
    void setUp() {
        tomorrow = LocalDate.now().plusDays(1);

        clinicianId = registerClinician("45678901232", "hekim1@test.com", "EMP-C1", "LIC-1");

        registrarId = registrarService.register(new ClinicRegistrarRegistrationRequest(
                "45678901244", "Zeynep", "Kaya", LocalDate.of(1995, 1, 1),
                "kayit@test.com", null, Gender.FEMALE,
                "EMP-R1", LocalDate.of(2023, 1, 1))).id();

        patientId = registerPatient("45678901256", "hasta1@test.com");

        availabilityId = createAvailability(clinicianId, at(9), at(10));
    }

    @Test
    void openSlots_createsAvailableSlotsForWholeAvailability() {
        List<AppointmentResponse> slots = openSlots(availabilityId);

        assertThat(slots).hasSize(3);
        assertThat(slots).extracting(AppointmentResponse::startTime)
                .containsExactly(at(9), at(9).plusMinutes(20), at(9).plusMinutes(40));
        assertThat(slots).allSatisfy(slot -> {
            assertThat(slot.status()).isEqualTo(AppointmentStatus.AVAILABLE);
            assertThat(slot.patientId()).isNull();
            assertThat(slot.clinicianId()).isEqualTo(clinicianId);
        });
    }

    @Test
    void openSlots_rejectsOpeningSameAvailabilityTwice() {
        openSlots(availabilityId);

        assertThatThrownBy(() -> openSlots(availabilityId))
                .isInstanceOf(AppointmentConflictException.class);
    }

    @Test
    void book_assignsPatientToSlot() {
        UUID slotId = openSlots(availabilityId).get(0).id();

        AppointmentResponse booked = appointmentService.book(slotId, new BookAppointmentRequest(patientId));

        assertThat(booked.status()).isEqualTo(AppointmentStatus.BOOKED);
        assertThat(booked.patientId()).isEqualTo(patientId);
    }

    @Test
    void book_rejectsSlotAlreadyBookedByAnotherPatient() {
        UUID slotId = openSlots(availabilityId).get(0).id();
        UUID otherPatientId = registerPatient("45678901270", "hasta2@test.com");

        appointmentService.book(slotId, new BookAppointmentRequest(patientId));

        assertThatThrownBy(() -> appointmentService.book(slotId, new BookAppointmentRequest(otherPatientId)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void book_rejectsOverlappingBookingForSamePatient() {
        UUID firstSlotId = openSlots(availabilityId).get(0).id();

        UUID otherClinicianId = registerClinician("45678901268", "hekim2@test.com", "EMP-C2", "LIC-2");
        UUID otherAvailabilityId = createAvailability(otherClinicianId, at(9), at(10));
        UUID sameTimeSlotId = openSlots(otherAvailabilityId).get(0).id();

        appointmentService.book(firstSlotId, new BookAppointmentRequest(patientId));

        assertThatThrownBy(() -> appointmentService.book(sameTimeSlotId, new BookAppointmentRequest(patientId)))
                .isInstanceOf(AppointmentConflictException.class);
    }

    @Test
    void release_makesSlotAvailableAgain() {
        UUID slotId = openSlots(availabilityId).get(0).id();
        appointmentService.book(slotId, new BookAppointmentRequest(patientId));

        AppointmentResponse released = appointmentService.release(slotId);

        assertThat(released.status()).isEqualTo(AppointmentStatus.AVAILABLE);
        assertThat(released.patientId()).isNull();
    }

    @Test
    void getByAvailability_doesNotCauseNPlusOne() {
        UUID bigAvailabilityId = createAvailability(clinicianId, at(13), at(17));
        List<AppointmentResponse> slots = openSlots(bigAvailabilityId);
        appointmentService.book(slots.get(0).id(), new BookAppointmentRequest(patientId));

        Statistics statistics = statistics();
        statistics.clear();

        List<AppointmentResponse> result = appointmentService.getByAvailability(bigAvailabilityId);

        assertThat(result).hasSize(12);
        assertThat(result).allSatisfy(slot -> assertThat(slot.clinicianId()).isEqualTo(clinicianId));
        assertThat(result.get(0).patientId()).isEqualTo(patientId);
        assertThat(statistics.getPrepareStatementCount()).isLessThanOrEqualTo(3);
    }

    private LocalDateTime at(int hour) {
        return tomorrow.atTime(hour, 0);
    }

    private UUID registerClinician(String nationalId, String email, String employeeNumber, String licenseNumber) {
        return clinicianService.register(new ClinicianRegistrationRequest(
                nationalId, "Hekim", "Demir", LocalDate.of(1980, 1, 1),
                email, null, Gender.MALE,
                employeeNumber, LocalDate.of(2020, 1, 1),
                Specialty.CARDIOLOGY, licenseNumber)).id();
    }

    private UUID registerPatient(String nationalId, String email) {
        return patientService.register(new PatientRegistrationRequest(
                nationalId, "Ayşe", "Yılmaz", LocalDate.of(1990, 1, 1),
                email, null, Gender.FEMALE, null, null)).id();
    }

    private UUID createAvailability(UUID clinicianId, LocalDateTime start, LocalDateTime end) {
        return availabilityService.create(clinicianId,
                new CreateAvailabilityRequest(start, end, SlotDuration.MIN_20)).id();
    }

    private List<AppointmentResponse> openSlots(UUID availabilityId) {
        return appointmentService.openSlots(availabilityId, new OpenSlotsRequest(registrarId));
    }
}
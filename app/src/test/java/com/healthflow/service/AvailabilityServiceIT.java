package com.healthflow.service;

import com.healthflow.IntegrationTestBase;
import com.healthflow.dto.availability.AvailabilityResponse;
import com.healthflow.dto.availability.CreateAvailabilityRequest;
import com.healthflow.dto.clinician.ClinicianRegistrationRequest;
import com.healthflow.entity.availability.AvailabilityStatus;
import com.healthflow.entity.availability.SlotDuration;
import com.healthflow.entity.user.Gender;
import com.healthflow.entity.staff.Specialty;
import com.healthflow.exception.AvailabilityConflictException;
import com.healthflow.exception.InvalidAvailabilityException;
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

class AvailabilityServiceIT extends IntegrationTestBase {

    @Autowired
    AvailabilityService availabilityService;

    @Autowired
    ClinicianService clinicianService;

    @Autowired
    StaffService staffService;

    UUID clinicianId;
    LocalDate tomorrow;

    @BeforeEach
    void setUp() {
        clinicianId = clinicianService.register(new ClinicianRegistrationRequest(
                "34567890128", "Hekim", "Demir", LocalDate.of(1980, 1, 1),
                "hekim@test.com", null, Gender.MALE,
                "EMP-1", LocalDate.of(2020, 1, 1),
                Specialty.CARDIOLOGY, "LIC-1")).id();

        tomorrow = LocalDate.now().plusDays(1);
    }

    @Test
    void create_savesActiveAvailability() {
        AvailabilityResponse response = availabilityService.create(clinicianId,
                request(at(9), at(10), SlotDuration.MIN_20));

        assertThat(response.status()).isEqualTo(AvailabilityStatus.ACTIVE);
        assertThat(response.clinicianId()).isEqualTo(clinicianId);
        assertThat(countRows("availabilities")).isEqualTo(1);
    }

    @Test
    void create_rejectsOverlappingAvailability() {
        availabilityService.create(clinicianId, request(at(9), at(12), SlotDuration.MIN_20));

        assertThatThrownBy(() -> availabilityService.create(clinicianId,
                request(at(11), at(13), SlotDuration.MIN_20)))
                .isInstanceOf(AvailabilityConflictException.class);

        assertThat(countRows("availabilities")).isEqualTo(1);
    }

    @Test
    void create_allowsAdjacentAvailability() {
        availabilityService.create(clinicianId, request(at(9), at(10), SlotDuration.MIN_20));
        availabilityService.create(clinicianId, request(at(10), at(11), SlotDuration.MIN_20));

        assertThat(countRows("availabilities")).isEqualTo(2);
    }

    @Test
    void create_rejectsClinicianOnLeave() {
        staffService.startLeave(clinicianId);

        assertThatThrownBy(() -> availabilityService.create(clinicianId,
                request(at(9), at(10), SlotDuration.MIN_20)))
                .isInstanceOf(InvalidAvailabilityException.class);

        assertThat(countRows("availabilities")).isZero();
    }

    @Test
    void create_rejectsLengthNotDivisibleBySlotDuration() {
        assertThatThrownBy(() -> availabilityService.create(clinicianId,
                request(at(9), at(9).plusMinutes(50), SlotDuration.MIN_20)))
                .isInstanceOf(InvalidAvailabilityException.class);

        assertThat(countRows("availabilities")).isZero();
    }

    @Test
    void getActiveByClinician_doesNotCauseNPlusOne() {
        for (int hour = 8; hour < 18; hour++) {
            availabilityService.create(clinicianId, request(at(hour), at(hour + 1), SlotDuration.MIN_30));
        }

        Statistics statistics = statistics();
        statistics.clear();

        List<AvailabilityResponse> availabilities = availabilityService.getActiveByClinician(clinicianId);

        assertThat(availabilities).hasSize(10);
        assertThat(availabilities).allSatisfy(a -> assertThat(a.clinicianId()).isEqualTo(clinicianId));
        assertThat(statistics.getPrepareStatementCount()).isLessThanOrEqualTo(2);
    }

    private LocalDateTime at(int hour) {
        return tomorrow.atTime(hour, 0);
    }

    private CreateAvailabilityRequest request(LocalDateTime start, LocalDateTime end, SlotDuration duration) {
        return new CreateAvailabilityRequest(start, end, duration);
    }
}
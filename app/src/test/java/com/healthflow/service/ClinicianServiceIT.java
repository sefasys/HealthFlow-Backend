package com.healthflow.service;

import com.healthflow.IntegrationTestBase;
import com.healthflow.dto.clinician.ClinicianRegistrationRequest;
import com.healthflow.dto.clinician.ClinicianResponse;
import com.healthflow.entity.user.Gender;
import com.healthflow.entity.staff.EmploymentStatus;
import com.healthflow.entity.staff.Specialty;
import com.healthflow.exception.ClinicianAlreadyExistsException;
import com.healthflow.exception.StaffAlreadyExistsException;
import com.healthflow.repository.IClinicianRepository;
import com.healthflow.repository.IStaffRepository;
import com.healthflow.repository.IUserRepository;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClinicianServiceIT extends IntegrationTestBase {

    @Autowired
    ClinicianService clinicianService;

    @Autowired
    IClinicianRepository clinicianRepository;

    @Autowired
    IStaffRepository staffRepository;

    @Autowired
    IUserRepository userRepository;

    @Test
    void register_createsUserStaffAndClinician() {
        ClinicianResponse response = clinicianService.register(
                request("23456780016", "hekim@test.com", "EMP-1", "LIC-1"));

        assertThat(response.id()).isNotNull();
        assertThat(response.employmentStatus()).isEqualTo(EmploymentStatus.ACTIVE);
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(staffRepository.count()).isEqualTo(1);
        assertThat(clinicianRepository.count()).isEqualTo(1);

        Integer roleCount = jdbcTemplate.queryForObject(
                "select count(*) from user_roles where role = 'CLINICIAN'", Integer.class);
        assertThat(roleCount).isEqualTo(1);
    }

    @Test
    void register_rejectsDuplicateLicenseNumber() {
        clinicianService.register(request("23456780026", "a@test.com", "EMP-1", "LIC-SAME"));

        assertThatThrownBy(() -> clinicianService.register(
                request("23456780036", "b@test.com", "EMP-2", "LIC-SAME")))
                .isInstanceOf(ClinicianAlreadyExistsException.class);

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(clinicianRepository.count()).isEqualTo(1);
    }

    @Test
    void register_rejectsDuplicateEmployeeNumber() {
        clinicianService.register(request("23456780046", "c@test.com", "EMP-SAME", "LIC-1"));

        assertThatThrownBy(() -> clinicianService.register(
                request("23456780056", "d@test.com", "EMP-SAME", "LIC-2")))
                .isInstanceOf(StaffAlreadyExistsException.class);

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(staffRepository.count()).isEqualTo(1);
    }

    @Test
    void getActiveBySpecialty_loadsStaffAndUserInSingleQuery() {
        for (int i = 1; i <= 15; i++) {
            clinicianService.register(request(
                    "2345678" + String.format("%03d", i) + "6",
                    "hekim" + i + "@test.com",
                    "EMP-" + i,
                    "LIC-" + i));
        }

        Statistics statistics = statistics();
        statistics.clear();

        List<ClinicianResponse> clinicians = clinicianService.getActiveBySpecialty(Specialty.CARDIOLOGY);

        assertThat(clinicians).hasSize(15);
        assertThat(clinicians).allSatisfy(c -> {
            assertThat(c.name()).isEqualTo("Hekim");
            assertThat(c.employeeNumber()).isNotNull();
        });
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
    }

    private ClinicianRegistrationRequest request(String nationalId, String email,
                                                 String employeeNumber, String licenseNumber) {
        return new ClinicianRegistrationRequest(
                nationalId, "Hekim", "Demir", LocalDate.of(1980, 1, 1),
                email, null, Gender.MALE,
                employeeNumber, LocalDate.of(2020, 1, 1),
                Specialty.CARDIOLOGY, licenseNumber);
    }
}
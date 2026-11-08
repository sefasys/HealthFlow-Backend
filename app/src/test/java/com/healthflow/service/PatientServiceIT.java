package com.healthflow.service;

import com.healthflow.IntegrationTestBase;

import com.healthflow.dto.patient.PatientRegistrationRequest;
import com.healthflow.dto.patient.PatientResponse;
import com.healthflow.entity.user.Gender;
import com.healthflow.repository.IPatientRepository;
import com.healthflow.repository.IUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PatientServiceIT extends IntegrationTestBase {

    @Autowired
    PatientService patientService;

    @Autowired
    IPatientRepository patientRepository;

    @Autowired
    IUserRepository userRepository;

    @AfterEach
    void cleanUp() {
        patientRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_savesPatientToDatabase() {
        var request = new PatientRegistrationRequest(
                "12345678902", "Ayşe", "Yılmaz", LocalDate.of(1990, 5, 12),
                "ayse@test.com", "+905551112233", Gender.FEMALE, null, null);

        PatientResponse response = patientService.register(request);

        assertThat(response.id()).isNotNull();
        assertThat(patientRepository.count()).isEqualTo(1);
        assertThat(userRepository.existsByNationalId("12345678902")).isTrue();
    }

    @Test
    void register_rollsBackUser_whenPatientDataViolatesDatabaseConstraint() {
        String tooLongAllergy = "a".repeat(101);

        var request = new PatientRegistrationRequest(
                "12345678904", "Mehmet", "Demir", LocalDate.of(1985, 3, 20),
                "mehmet@test.com", "+905552223344", Gender.MALE, null,
                Set.of(tooLongAllergy));

        assertThatThrownBy(() -> patientService.register(request))
                .isInstanceOf(DataAccessException.class);

        assertThat(userRepository.existsByNationalId("12345678904")).isFalse();
        assertThat(userRepository.count()).isZero();
        assertThat(patientRepository.count()).isZero();
    }


}
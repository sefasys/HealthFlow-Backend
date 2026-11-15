package com.healthflow.service;

import com.healthflow.IntegrationTestBase;
import com.healthflow.dto.common.PageResponse;
import com.healthflow.dto.patient.PatientRegistrationRequest;
import com.healthflow.dto.patient.PatientResponse;
import com.healthflow.entity.user.Gender;
import com.healthflow.exception.UserAlreadyExistsException;
import com.healthflow.repository.IPatientRepository;
import com.healthflow.repository.IUserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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


    // Bu class IntelliJ üzerinden çalışmıyor çünkü docker üzerinden çalıştırıyoruz bağlanamadığı için terminal üzerinden integration test yapıyoruz
    // o aşamada 4 metotta başarılı şekilde çalıştı bu da demek oluyor ki rollback mekanizması doğru şekilde çalıştı.
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

    @Test
    void register_rejectsDuplicateNationalId() {
        patientService.register(request("12345678906", "first@test.com"));

        assertThatThrownBy(() -> patientService.register(request("12345678906", "second@test.com")))
                .isInstanceOf(UserAlreadyExistsException.class);

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(patientRepository.count()).isEqualTo(1);
    }

    @Test
    void register_rejectsDuplicateEmail_ignoringCase() {
        patientService.register(request("12345678908", "same@test.com"));

        assertThatThrownBy(() -> patientService.register(request("12345678900", "SAME@test.com")))
                .isInstanceOf(UserAlreadyExistsException.class);

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void search_returnsCorrectPage_andTotals() {
        for (int i = 1; i <= 25; i++) {
            patientService.register(new PatientRegistrationRequest(
                    "1234567" + String.format("%03d", i) + "2",
                    "Hasta",
                    "Soyad" + String.format("%02d", i),
                    LocalDate.of(1990, 1, 1),
                    "hasta" + i + "@test.com",
                    null, Gender.FEMALE, null, null));
        }

        PageResponse<PatientResponse> firstPage = patientService.search(
                null, null, null, PageRequest.of(0, 10, Sort.by("user.surname")));

        assertThat(firstPage.content()).hasSize(10);
        assertThat(firstPage.totalElements()).isEqualTo(25);
        assertThat(firstPage.totalPages()).isEqualTo(3);
        assertThat(firstPage.content().get(0).surname()).isEqualTo("Soyad01");

        PageResponse<PatientResponse> lastPage = patientService.search(
                null, null, null, PageRequest.of(2, 10, Sort.by("user.surname")));

        assertThat(lastPage.content()).hasSize(5);
        assertThat(lastPage.content().get(4).surname()).isEqualTo("Soyad25");
    }

    @Test
    void search_doesNotCauseNPlusOne() {
        for (int i = 1; i <= 20; i++) {
            patientService.register(new PatientRegistrationRequest(
                    "7654321" + String.format("%03d", i) + "4",
                    "Hasta",
                    "Soyad" + String.format("%02d", i),
                    LocalDate.of(1990, 1, 1),
                    "nplus" + i + "@test.com",
                    null, Gender.MALE, null,
                    Set.of("Penisilin", "Fıstık")));
        }

        Statistics statistics = statistics();
        statistics.clear();

        PageResponse<PatientResponse> page = patientService.search(
                null, null, null, PageRequest.of(0, 20, Sort.by("user.surname")));

        assertThat(page.content()).hasSize(20);
        assertThat(page.content()).allSatisfy(p -> assertThat(p.allergies()).hasSize(2));
        assertThat(statistics.getPrepareStatementCount()).isLessThanOrEqualTo(3);
    }



    private PatientRegistrationRequest request(String nationalId, String email) {
        return new PatientRegistrationRequest(
                nationalId, "Ayşe", "Yılmaz", LocalDate.of(1990, 5, 12),
                email, "+905551112233", Gender.FEMALE, null, null);
    }
}
package com.healthflow.application.usecase.patient;

import static org.junit.jupiter.api.Assertions.*;

import com.healthflow.application.exception.InvalidSearchQueryException;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.infrastructure.repository.InMemoryIPatientRepository;
import com.healthflow.port.repository.IPatientRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SearchPatientUseCaseTest {

  private IPatientRepository patientRepository;
  private SearchPatientUseCase searchPatientUseCase;
  private UserFactory userFactory;

  @BeforeEach
  void setUp() {
    patientRepository = new InMemoryIPatientRepository();
    searchPatientUseCase = new SearchPatientUseCase(patientRepository);
    userFactory = new UserFactory();
  }

  @Test
  void shouldThrowInvalidSearchQueryExceptionWhenQueryIsNull() {

    assertThrows(InvalidSearchQueryException.class, () -> searchPatientUseCase.execute(null));
  }

  @Test
  void shouldThrowInvalidSearchQueryExceptionWhenQueryIsBlank() {

    assertThrows(InvalidSearchQueryException.class, () -> searchPatientUseCase.execute("   "));
  }

  @Test
  void shouldReturnMatchingPatients() {

    User user =
        userFactory.createUser(
            new NationalId("12345678910"),
            "Sefa",
            "Soysal",
            LocalDate.of(2004, 1, 1),
            "sefa@example.com",
            "5555555555",
            UserRole.PATIENT);

    Patient patient = new Patient(user);

    patientRepository.addPatient(patient);

    List<Patient> result = searchPatientUseCase.execute("Sefa");

    assertEquals(1, result.size());
    assertEquals("Sefa", result.getFirst().getUser().getName());
  }

  @Test
  void shouldReturnEmptyListWhenNoPatientMatches() {

    List<Patient> result = searchPatientUseCase.execute("Unknown");

    assertTrue(result.isEmpty());
  }
}

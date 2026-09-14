package com.healthflow.application.usecase.patient;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.healthflow.application.exception.PatientAlreadyExistsException;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.infrastructure.repository.InMemoryIPatientRepository;
import com.healthflow.port.repository.IPatientRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreatePatientUseCaseTest {
  private IPatientRepository patientRepository;
  private UserFactory userFactory;
  private CreatePatientUseCase createPatientUseCase;

  @BeforeEach
  void setUp() {
    patientRepository = new InMemoryIPatientRepository();
    userFactory = new UserFactory();

    createPatientUseCase = new CreatePatientUseCase(patientRepository, userFactory);
  }

  @Test
  void shouldThrowPatientAlreadyExistsExceptionWhenNationalIdAlreadyExists() {
    // arrange
    NationalId nationalId = new NationalId("12345678910");

    createPatientUseCase.execute(
        nationalId, "Sefa", "Soysal", LocalDate.of(2004, 1, 1), "sefa@example.com", "5555555555");

    // act + assert
    assertThrows(
        PatientAlreadyExistsException.class,
        () ->
            createPatientUseCase.execute(
                nationalId,
                "Another",
                "Patient",
                LocalDate.of(2000, 1, 1),
                "another@example.com",
                "5551112233"));
  }
}

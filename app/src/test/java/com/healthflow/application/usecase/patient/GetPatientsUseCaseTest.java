package com.healthflow.application.usecase.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

public class GetPatientsUseCaseTest {

  private IPatientRepository patientRepository;
  private GetPatientsUseCase getPatientsUseCase;
  private UserFactory userFactory;

  @BeforeEach
  void setUp() {
    patientRepository = new InMemoryIPatientRepository();
    getPatientsUseCase = new GetPatientsUseCase(patientRepository);
    userFactory = new UserFactory();
  }

  @Test
  void shouldReturnAllPatients() {

    User user1 =
        userFactory.createUser(
            new NationalId("12345678910"),
            "Sefa",
            "Soysal",
            LocalDate.of(2004, 1, 1),
            "sefa@example.com",
            "5555555555",
            UserRole.PATIENT);

    User user2 =
        userFactory.createUser(
            new NationalId("12345678912"),
            "Ahmet",
            "Yılmaz",
            LocalDate.of(1995, 5, 10),
            "ahmet@example.com",
            "5554443322",
            UserRole.PATIENT);

    patientRepository.addPatient(new Patient(user1));
    patientRepository.addPatient(new Patient(user2));

    List<Patient> result = getPatientsUseCase.execute();

    assertEquals(2, result.size());
  }

  @Test
  void shouldReturnEmptyListWhenNoPatientsExist() {

    List<Patient> result = getPatientsUseCase.execute();

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnCorrectPatients() {

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

    List<Patient> result = getPatientsUseCase.execute();

    assertEquals(1, result.size());
    assertEquals("Sefa", result.getFirst().getUser().getName());
    assertEquals("Soysal", result.getFirst().getUser().getSurname());
  }
}

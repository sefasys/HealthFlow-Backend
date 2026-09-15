package com.healthflow.application.usecase.patient;

import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.infrastructure.repository.InMemoryIPatientRepository;
import com.healthflow.port.repository.IPatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FindByUniqueIdUseCaseTest {

    private IPatientRepository patientRepository;
    private FindPatientByUniqueIdUseCase findPatientByUniqueIdUseCase;
    private UserFactory userFactory;

    @BeforeEach
    void setUp() {
        patientRepository = new InMemoryIPatientRepository();
        findPatientByUniqueIdUseCase =
                new FindPatientByUniqueIdUseCase(patientRepository);
        userFactory = new UserFactory();
    }

    @Test
    void shouldReturnPatientWhenUniqueIdExists() {

        User user = userFactory.createUser(
                new NationalId("12345678910"),
                "Sefa",
                "Soysal",
                LocalDate.of(2004, 1, 1),
                "sefa@example.com",
                "5555555555",
                UserRole.PATIENT
        );

        Patient patient = new Patient(user);

        patientRepository.addPatient(patient);

        UUID uniqueId = user.getUniqueID();

        Optional<Patient> result =
                findPatientByUniqueIdUseCase.execute(uniqueId);

        assertTrue(result.isPresent());
        assertEquals(
                uniqueId,
                result.get().getUser().getUniqueID()
        );
    }

    @Test
    void shouldReturnEmptyWhenUniqueIdDoesNotExist() {

        UUID uniqueId = UUID.randomUUID();

        Optional<Patient> result =
                findPatientByUniqueIdUseCase.execute(uniqueId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenUniqueIdIsNull() {

        assertThrows(
                InvalidUniqueIdException.class,
                () -> findPatientByUniqueIdUseCase.execute(null)
        );
    }
}
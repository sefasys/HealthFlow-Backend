package com.healthflow.application.usecase.patient;

import com.healthflow.application.exception.PatientNotFoundException;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FindPatientByUniqueIdUseCaseTest {

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

        UUID uniqueId = user.getUniqueId();

        Patient result =
                findPatientByUniqueIdUseCase.execute(uniqueId);

        assertEquals(
                uniqueId,
                result.getUser().getUniqueId()
        );
    }

    @Test
    void shouldThrowPatientNotFoundExceptionWhenUniqueIdDoesNotExist() {

        UUID uniqueId = UUID.randomUUID();

        assertThrows(
                PatientNotFoundException.class,
                () -> findPatientByUniqueIdUseCase.execute(uniqueId)
        );
    }

    @Test
    void shouldThrowExceptionWhenUniqueIdIsNull() {

        assertThrows(
                InvalidUniqueIdException.class, // kendi custom exception'ın varsa onu yaz
                () -> findPatientByUniqueIdUseCase.execute(null)
        );
    }
}
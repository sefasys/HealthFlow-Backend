package com.healthflow.application.usecase.patient;

import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.domain.exception.InvalidNationalIdException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FindPatientByNationalIdUseCaseTest {

    private IPatientRepository patientRepository;
    private FindPatientByNationalIdUseCase findPatientByNationalIdUseCase;
    private UserFactory userFactory;

    @BeforeEach
    void setUp() {
        patientRepository = new InMemoryIPatientRepository();
        findPatientByNationalIdUseCase =
                new FindPatientByNationalIdUseCase(patientRepository);
        userFactory = new UserFactory();
    }

    @Test
    void shouldReturnPatientWhenNationalIdExists() {

        NationalId nationalId =
                new NationalId("12345678910");

        User user = userFactory.createUser(
                nationalId,
                "Sefa",
                "Soysal",
                LocalDate.of(2004, 1, 1),
                "sefa@example.com",
                "5555555555",
                UserRole.PATIENT
        );

        Patient patient = new Patient(user);
        patientRepository.addPatient(patient);

        Patient result =
                findPatientByNationalIdUseCase.execute(nationalId);

        assertEquals(
                nationalId,
                result.getUser().getNationalId()
        );
    }

    @Test
    void shouldThrowPatientNotFoundExceptionWhenNationalIdDoesNotExist() {

        NationalId nationalId =
                new NationalId("12345678910");

        assertThrows(
                PatientNotFoundException.class,
                () -> findPatientByNationalIdUseCase.execute(nationalId)
        );
    }

    @Test
    void shouldThrowExceptionWhenNationalIdIsNull() {

        assertThrows(
                InvalidNationalIdException.class, // custom exception'ın hangisiyse değiştir
                () -> findPatientByNationalIdUseCase.execute(null)
        );
    }
}
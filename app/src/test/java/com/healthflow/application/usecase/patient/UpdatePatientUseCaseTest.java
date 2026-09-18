package com.healthflow.application.usecase.patient;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.application.exception.InvalidUpdateRequestException;
import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.port.repository.IPatientRepository;
import com.healthflow.domain.model.user.patient.BloodType;
import com.healthflow.domain.model.user.patient.Patient;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdatePatientUseCaseTest {

    private IPatientRepository patientRepository;
    private UpdatePatientUseCase updatePatientUseCase;

    @BeforeEach
    void setUp() {
        patientRepository = mock(IPatientRepository.class);
        updatePatientUseCase = new UpdatePatientUseCase(patientRepository);
    }

    @Test
    void shouldThrowInvalidUniqueIdExceptionWhenUniqueIdIsNull() {

        assertThrows(
                InvalidUniqueIdException.class,
                () -> updatePatientUseCase.execute(
                        null,
                        "newmail@example.com",
                        null,
                        null
                )
        );

        verifyNoInteractions(patientRepository);
    }

    @Test
    void shouldThrowInvalidUpdateRequestExceptionWhenNoFieldIsProvided() {

        UUID uniqueId = UUID.randomUUID();

        assertThrows(
                InvalidUpdateRequestException.class,
                () -> updatePatientUseCase.execute(
                        uniqueId,
                        null,
                        null,
                        null
                )
        );

        verifyNoInteractions(patientRepository);
    }

    @Test
    void shouldThrowPatientNotFoundExceptionWhenPatientDoesNotExist() {

        UUID uniqueId = UUID.randomUUID();

        when(patientRepository.findByUniqueId(uniqueId))
                .thenReturn(Optional.empty());

        assertThrows(
                PatientNotFoundException.class,
                () -> updatePatientUseCase.execute(
                        uniqueId,
                        "newmail@example.com",
                        null,
                        null
                )
        );

        verify(patientRepository).findByUniqueId(uniqueId);
        verify(patientRepository, never()).update(any());
    }

    @Test
    void shouldUpdateEmailWhenEmailIsProvided() {

        UUID uniqueId = UUID.randomUUID();
        Patient patient = mock(Patient.class);

        when(patientRepository.findByUniqueId(uniqueId))
                .thenReturn(Optional.of(patient));

        Patient result = updatePatientUseCase.execute(
                uniqueId,
                "newmail@example.com",
                null,
                null
        );

        verify(patient).updateEmail("newmail@example.com");
        verify(patient, never()).updatePhoneNumber(anyString());
        verify(patient, never()).updateBloodType(any());

        verify(patientRepository).update(patient);

        assertSame(patient, result);
    }

    @Test
    void shouldUpdatePhoneNumberWhenPhoneNumberIsProvided() {

        UUID uniqueId = UUID.randomUUID();
        Patient patient = mock(Patient.class);

        when(patientRepository.findByUniqueId(uniqueId))
                .thenReturn(Optional.of(patient));

        Patient result = updatePatientUseCase.execute(
                uniqueId,
                null,
                "5555555555",
                null
        );

        verify(patient).updatePhoneNumber("5555555555");
        verify(patient, never()).updateEmail(anyString());
        verify(patient, never()).updateBloodType(any());

        verify(patientRepository).update(patient);

        assertSame(patient, result);
    }

    @Test
    void shouldUpdateBloodTypeWhenBloodTypeIsProvided() {

        UUID uniqueId = UUID.randomUUID();
        Patient patient = mock(Patient.class);
        BloodType bloodType = BloodType.values()[0];

        when(patientRepository.findByUniqueId(uniqueId))
                .thenReturn(Optional.of(patient));

        Patient result = updatePatientUseCase.execute(
                uniqueId,
                null,
                null,
                bloodType
        );

        verify(patient).updateBloodType(bloodType);
        verify(patient, never()).updateEmail(anyString());
        verify(patient, never()).updatePhoneNumber(anyString());

        verify(patientRepository).update(patient);

        assertSame(patient, result);
    }

    @Test
    void shouldUpdateAllProvidedFields() {

        UUID uniqueId = UUID.randomUUID();
        Patient patient = mock(Patient.class);
        BloodType bloodType = BloodType.values()[0];

        when(patientRepository.findByUniqueId(uniqueId))
                .thenReturn(Optional.of(patient));

        Patient result = updatePatientUseCase.execute(
                uniqueId,
                "newmail@example.com",
                "5555555555",
                bloodType
        );

        verify(patient).updateEmail("newmail@example.com");
        verify(patient).updatePhoneNumber("5555555555");
        verify(patient).updateBloodType(bloodType);

        verify(patientRepository).update(patient);

        assertSame(patient, result);
    }
}
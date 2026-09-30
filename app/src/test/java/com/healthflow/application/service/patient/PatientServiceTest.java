package com.healthflow.application.service.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.healthflow.application.exception.InvalidUpdateRequestException;
import com.healthflow.application.exception.InvalidSearchQueryException;
import com.healthflow.application.exception.PatientAlreadyExistsException;
import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.application.service.PatientService;
import com.healthflow.domain.exception.InvalidNationalIdException;
import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.patient.BloodType;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import com.healthflow.presentation.dto.patient.CreatePatientRequestDto;
import com.healthflow.presentation.dto.patient.PatientResponseDto;
import com.healthflow.presentation.dto.patient.UpdatePatientRequestDto;
import com.healthflow.application.mapper.PatientMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PatientService unit testi. Repository ve mapper mock'lanır; burada sadece service'in iş
 * kuralları test edilir (duplicate kontrolü, bulunamayan hasta, boş istek vb.). Mapper'ın alan
 * eşlemesi ayrı bir PatientMapperTest'in konusudur.
 */
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock private IPatientRepository patientRepository;
    @Mock private PatientMapper patientMapper;

    private PatientService patientService;

    private final PatientResponseDto sampleResponse =
            new PatientResponseDto(UUID.randomUUID(), "Sefa", "Soysal", LocalDate.of(2004, 1, 1));

    @BeforeEach
    void setUp() {
        // UserFactory saf bir factory olduğu için gerçeğini kullanıyoruz.
        patientService = new PatientService(patientRepository, new UserFactory(), patientMapper);
    }

    // ==============================
    // createPatient
    // ==============================

    @Test
    void shouldCreatePatientAndReturnResponseDto() {

        CreatePatientRequestDto request =
                new CreatePatientRequestDto(
                        "12345678910",
                        "Sefa",
                        "Soysal",
                        LocalDate.of(2004, 1, 1),
                        "sefa@example.com",
                        "5555555555");

        when(patientRepository.findByNationalId(new NationalId("12345678910")))
                .thenReturn(Optional.empty());
        when(patientMapper.toResponseDto(any(Patient.class))).thenReturn(sampleResponse);

        PatientResponseDto result = patientService.createPatient(request);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).addPatient(captor.capture());

        Patient saved = captor.getValue();
        assertEquals(new NationalId("12345678910"), saved.getUser().getNationalId());
        assertEquals("Sefa", saved.getUser().getName());
        assertEquals("Soysal", saved.getUser().getSurname());

        assertSame(sampleResponse, result);
    }

    @Test
    void shouldThrowPatientAlreadyExistsExceptionWhenNationalIdAlreadyExists() {

        CreatePatientRequestDto request =
                new CreatePatientRequestDto(
                        "12345678910",
                        "Another",
                        "Patient",
                        LocalDate.of(2000, 1, 1),
                        "another@example.com",
                        "5551112233");

        when(patientRepository.findByNationalId(new NationalId("12345678910")))
                .thenReturn(Optional.of(mock(Patient.class)));

        assertThrows(PatientAlreadyExistsException.class, () -> patientService.createPatient(request));

        verify(patientRepository, never()).addPatient(any());
        verifyNoInteractions(patientMapper);
    }

    // ==============================
    // getPatients
    // ==============================

    @Test
    void shouldReturnAllPatientsAsDtos() {

        Patient patient1 = mock(Patient.class);
        Patient patient2 = mock(Patient.class);
        PatientResponseDto dto1 =
                new PatientResponseDto(UUID.randomUUID(), "Sefa", "Soysal", LocalDate.of(2004, 1, 1));
        PatientResponseDto dto2 =
                new PatientResponseDto(UUID.randomUUID(), "Ahmet", "Yılmaz", LocalDate.of(1995, 5, 10));

        when(patientRepository.getPatients()).thenReturn(List.of(patient1, patient2));
        when(patientMapper.toResponseDto(patient1)).thenReturn(dto1);
        when(patientMapper.toResponseDto(patient2)).thenReturn(dto2);

        List<PatientResponseDto> result = patientService.getPatients();

        assertEquals(List.of(dto1, dto2), result);
    }

    @Test
    void shouldReturnEmptyListWhenNoPatientsExist() {

        when(patientRepository.getPatients()).thenReturn(List.of());

        List<PatientResponseDto> result = patientService.getPatients();

        assertTrue(result.isEmpty());
        verifyNoInteractions(patientMapper);
    }

    // ==============================
    // findPatientByUniqueId
    // ==============================

    @Test
    void shouldReturnPatientWhenUniqueIdExists() {

        UUID uniqueId = UUID.randomUUID();
        Patient patient = mock(Patient.class);

        when(patientRepository.findByUniqueId(uniqueId)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDto(patient)).thenReturn(sampleResponse);

        PatientResponseDto result = patientService.findPatientByUniqueId(uniqueId);

        assertSame(sampleResponse, result);
    }

    @Test
    void shouldThrowPatientNotFoundExceptionWhenUniqueIdDoesNotExist() {

        UUID uniqueId = UUID.randomUUID();

        when(patientRepository.findByUniqueId(uniqueId)).thenReturn(Optional.empty());

        assertThrows(
                PatientNotFoundException.class, () -> patientService.findPatientByUniqueId(uniqueId));

        verifyNoInteractions(patientMapper);
    }

    // ==============================
    // findPatientByNationalId
    // ==============================

    @Test
    void shouldReturnPatientWhenNationalIdExists() {

        Patient patient = mock(Patient.class);

        when(patientRepository.findByNationalId(new NationalId("12345678910")))
                .thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDto(patient)).thenReturn(sampleResponse);

        PatientResponseDto result = patientService.findPatientByNationalId("12345678910");

        assertSame(sampleResponse, result);
    }

    @Test
    void shouldThrowPatientNotFoundExceptionWhenNationalIdDoesNotExist() {

        when(patientRepository.findByNationalId(new NationalId("12345678910")))
                .thenReturn(Optional.empty());

        assertThrows(
                PatientNotFoundException.class,
                () -> patientService.findPatientByNationalId("12345678910"));

        verifyNoInteractions(patientMapper);
    }

    // ==============================
    // searchPatient
    // ==============================

    @Test
    void shouldThrowInvalidSearchQueryExceptionWhenQueryIsNull() {

        assertThrows(InvalidSearchQueryException.class, () -> patientService.searchPatient(null));

        verifyNoInteractions(patientRepository);
    }

    @Test
    void shouldThrowInvalidSearchQueryExceptionWhenQueryIsBlank() {

        assertThrows(InvalidSearchQueryException.class, () -> patientService.searchPatient("   "));

        verifyNoInteractions(patientRepository);
    }

    @Test
    void shouldReturnMatchingPatients() {

        Patient patient = mock(Patient.class);

        when(patientRepository.search("Sefa")).thenReturn(List.of(patient));
        when(patientMapper.toResponseDto(patient)).thenReturn(sampleResponse);

        List<PatientResponseDto> result = patientService.searchPatient("Sefa");

        assertEquals(List.of(sampleResponse), result);
    }

    @Test
    void shouldReturnEmptyListWhenNoPatientMatches() {

        when(patientRepository.search("Unknown")).thenReturn(List.of());

        List<PatientResponseDto> result = patientService.searchPatient("Unknown");

        assertTrue(result.isEmpty());
    }

    // ==============================
    // updatePatient
    // ==============================

    @Test
    void shouldThrowInvalidUpdateRequestExceptionWhenNoFieldIsProvided() {

        UUID uniqueId = UUID.randomUUID();
        UpdatePatientRequestDto request = new UpdatePatientRequestDto(null, null, null);

        assertThrows(
                InvalidUpdateRequestException.class, () -> patientService.updatePatient(uniqueId, request));

        verifyNoInteractions(patientRepository);
    }

    @Test
    void shouldThrowPatientNotFoundExceptionWhenUpdatingNonExistingPatient() {

        UUID uniqueId = UUID.randomUUID();
        UpdatePatientRequestDto request =
                new UpdatePatientRequestDto("newmail@example.com", null, null);

        when(patientRepository.findByUniqueId(uniqueId)).thenReturn(Optional.empty());

        assertThrows(
                PatientNotFoundException.class, () -> patientService.updatePatient(uniqueId, request));

        verify(patientRepository, never()).update(any());
    }

    @Test
    void shouldUpdateEmailWhenEmailIsProvided() {

        UUID uniqueId = UUID.randomUUID();
        Patient patient = mock(Patient.class);

        when(patientRepository.findByUniqueId(uniqueId)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDto(patient)).thenReturn(sampleResponse);

        PatientResponseDto result =
                patientService.updatePatient(
                        uniqueId, new UpdatePatientRequestDto("newmail@example.com", null, null));

        verify(patient).updateEmail("newmail@example.com");
        verify(patient, never()).updatePhoneNumber(anyString());
        verify(patient, never()).updateBloodType(any());
        verify(patientRepository).update(patient);

        assertSame(sampleResponse, result);
    }

    @Test
    void shouldUpdatePhoneNumberWhenPhoneNumberIsProvided() {

        UUID uniqueId = UUID.randomUUID();
        Patient patient = mock(Patient.class);

        when(patientRepository.findByUniqueId(uniqueId)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDto(patient)).thenReturn(sampleResponse);

        PatientResponseDto result =
                patientService.updatePatient(
                        uniqueId, new UpdatePatientRequestDto(null, "5555555555", null));

        verify(patient).updatePhoneNumber("5555555555");
        verify(patient, never()).updateEmail(anyString());
        verify(patient, never()).updateBloodType(any());
        verify(patientRepository).update(patient);

        assertSame(sampleResponse, result);
    }

    @Test
    void shouldUpdateBloodTypeWhenBloodTypeIsProvided() {

        UUID uniqueId = UUID.randomUUID();
        Patient patient = mock(Patient.class);
        BloodType bloodType = BloodType.values()[0];

        when(patientRepository.findByUniqueId(uniqueId)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDto(patient)).thenReturn(sampleResponse);

        PatientResponseDto result =
                patientService.updatePatient(uniqueId, new UpdatePatientRequestDto(null, null, bloodType));

        verify(patient).updateBloodType(bloodType);
        verify(patient, never()).updateEmail(anyString());
        verify(patient, never()).updatePhoneNumber(anyString());
        verify(patientRepository).update(patient);

        assertSame(sampleResponse, result);
    }

    @Test
    void shouldUpdateAllProvidedFields() {

        UUID uniqueId = UUID.randomUUID();
        Patient patient = mock(Patient.class);
        BloodType bloodType = BloodType.values()[0];

        when(patientRepository.findByUniqueId(uniqueId)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDto(patient)).thenReturn(sampleResponse);

        PatientResponseDto result =
                patientService.updatePatient(
                        uniqueId, new UpdatePatientRequestDto("newmail@example.com", "5555555555", bloodType));

        verify(patient).updateEmail("newmail@example.com");
        verify(patient).updatePhoneNumber("5555555555");
        verify(patient).updateBloodType(bloodType);
        verify(patientRepository).update(patient);

        assertSame(sampleResponse, result);
    }

    // ==============================
    // Null guard testleri
    // Service'ten ilgili null kontrollerini kaldırdıysan bu bölümü sil.
    // ==============================

    @Test
    void shouldThrowInvalidUniqueIdExceptionWhenFindingWithNullUniqueId() {

        assertThrows(InvalidUniqueIdException.class, () -> patientService.findPatientByUniqueId(null));
    }

    @Test
    void shouldThrowInvalidNationalIdExceptionWhenNationalIdIsNull() {

        assertThrows(
                InvalidNationalIdException.class, () -> patientService.findPatientByNationalId(null));
    }

    @Test
    void shouldThrowInvalidUniqueIdExceptionWhenUpdatingWithNullUniqueId() {

        assertThrows(
                InvalidUniqueIdException.class,
                () ->
                        patientService.updatePatient(
                                null, new UpdatePatientRequestDto("newmail@example.com", null, null)));
    }
}
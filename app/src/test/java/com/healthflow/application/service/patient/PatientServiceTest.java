package com.healthflow.application.service.patient;

import com.healthflow.application.dto.patient.*;
import com.healthflow.application.exception.*;
import com.healthflow.application.mapper.patient.PatientMapper;
import com.healthflow.application.service.PatientService;
import com.healthflow.application.service.UserService;
import com.healthflow.domain.exception.InvalidPhoneNumberException;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.patient.BloodType;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.infrastructure.repository.InMemoryPatientRepository;
import com.healthflow.infrastructure.repository.InMemoryUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PatientServiceTest {

    private InMemoryPatientRepository patientRepository;
    private InMemoryUserRepository userRepository;
    private UserFactory userFactory;
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        patientRepository = new InMemoryPatientRepository();
        userRepository = new InMemoryUserRepository();
        userFactory = new UserFactory();

        UserService userService = new UserService(
                userRepository,
                userFactory
        );

        PatientMapper patientMapper =
                Mappers.getMapper(PatientMapper.class);

        patientService = new PatientService(
                patientRepository,
                userService,
                patientMapper
        );
    }

    @Test
    void createPatient_shouldSaveUserAndPatient() {
        PatientResponseDto response =
                patientService.createPatient(validCreateRequest());

        Patient patient = patientRepository
                .findByUniqueId(response.id())
                .orElseThrow();

        User user = userRepository
                .findByNationalId(new NationalId("10000000146"))
                .orElseThrow();

        assertAll(
                () -> assertEquals(user.getUniqueId(), response.id()),
                () -> assertSame(user, patient.getUser()),
                () -> assertEquals("Deniz", response.name()),
                () -> assertEquals("Yilmaz", response.surname()),
                () -> assertEquals(
                        LocalDate.of(2000, 5, 10),
                        response.birthDate()
                ),
                () -> assertEquals(
                        List.of(UserRole.PATIENT),
                        user.getUserRoleList()
                ),
                () -> assertEquals(
                        BloodType.UNKNOWN,
                        patient.getBloodType()
                ),
                () -> assertEquals(
                        1,
                        patientRepository.getPatients().size()
                )
        );
    }

    @Test
    void createPatient_shouldReuseExistingClinicianUserAndAddPatientRole() {
        User existingUser = userFactory.createUser(
                new NationalId("10000000146"),
                "Deniz",
                "Yilmaz",
                LocalDate.of(2000, 5, 10),
                "original@example.com",
                "05000000001",
                UserRole.CLINICIAN
        );

        userRepository.save(existingUser);

        PatientResponseDto response =
                patientService.createPatient(validCreateRequest());

        Patient patient = patientRepository
                .findByUniqueId(response.id())
                .orElseThrow();

        assertAll(
                () -> assertEquals(
                        existingUser.getUniqueId(),
                        response.id()
                ),
                () -> assertSame(existingUser, patient.getUser()),
                () -> assertTrue(
                        existingUser.getUserRoleList()
                                .contains(UserRole.CLINICIAN)
                ),
                () -> assertTrue(
                        existingUser.getUserRoleList()
                                .contains(UserRole.PATIENT)
                ),
                () -> assertEquals(
                        2,
                        existingUser.getUserRoleList().size()
                ),
                () -> assertEquals(
                        "original@example.com",
                        patient.getUser().getEmail()
                ),
                () -> assertEquals(
                        "05000000001",
                        patient.getUser().getPhoneNumber()
                )
        );
    }

    @Test
    void createPatient_shouldRejectDuplicateWithoutAddingAnotherPatient() {
        PatientResponseDto first =
                patientService.createPatient(validCreateRequest());

        assertThrows(
                PatientAlreadyExistsException.class,
                () -> patientService.createPatient(validCreateRequest())
        );

        assertAll(
                () -> assertEquals(
                        1,
                        patientRepository.getPatients().size()
                ),
                () -> assertEquals(
                        first.id(),
                        userRepository
                                .findByNationalId(
                                        new NationalId("10000000146")
                                )
                                .orElseThrow()
                                .getUniqueId()
                )
        );
    }

    @Test
    void findPatientByUniqueId_shouldReturnSavedPatient() {
        PatientResponseDto created =
                patientService.createPatient(validCreateRequest());

        PatientResponseDto found =
                patientService.findPatientByUniqueId(created.id());

        assertEquals(created, found);
    }

    @Test
    void findPatientByUniqueId_shouldRejectUnknownId() {
        assertThrows(
                PatientNotFoundException.class,
                () -> patientService.findPatientByUniqueId(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void findPatientByNationalId_shouldReturnSavedPatient() {
        PatientResponseDto created =
                patientService.createPatient(validCreateRequest());

        PatientResponseDto found =
                patientService.findPatientByNationalId(
                        new FindPatientByNationalIdRequestDto(
                                "10000000146"
                        )
                );

        assertEquals(created, found);
    }

    @Test
    void updatePatient_shouldChangeOnlyEmailWhenOtherFieldsAreAbsent() {
        PatientResponseDto created =
                patientService.createPatient(validCreateRequest());

        UpdatePatientResponseDto response = patientService.updatePatient(
                created.id(),
                new UpdatePatientRequestDto(
                        "new@example.com",
                        null,
                        null
                )
        );

        Patient stored = patientRepository
                .findByUniqueId(created.id())
                .orElseThrow();

        assertAll(
                () -> assertEquals(created.id(), response.id()),
                () -> assertEquals(
                        "new@example.com",
                        response.email()
                ),
                () -> assertEquals(
                        "new@example.com",
                        stored.getUser().getEmail()
                ),
                () -> assertEquals(
                        "05000000000",
                        stored.getUser().getPhoneNumber()
                ),
                () -> assertEquals(
                        BloodType.UNKNOWN,
                        stored.getBloodType()
                )
        );
    }

    @Test
    void updatePatient_shouldChangeOnlyBloodType() {
        PatientResponseDto created =
                patientService.createPatient(validCreateRequest());

        UpdatePatientResponseDto response = patientService.updatePatient(
                created.id(),
                new UpdatePatientRequestDto(
                        null,
                        null,
                        BloodType.A_POSITIVE
                )
        );

        Patient stored = patientRepository
                .findByUniqueId(created.id())
                .orElseThrow();

        assertAll(
                () -> assertEquals(
                        BloodType.A_POSITIVE,
                        response.bloodType()
                ),
                () -> assertEquals(
                        BloodType.A_POSITIVE,
                        stored.getBloodType()
                ),
                () -> assertEquals(
                        "deniz@example.com",
                        stored.getUser().getEmail()
                ),
                () -> assertEquals(
                        "05000000000",
                        stored.getUser().getPhoneNumber()
                )
        );
    }

    @Test
    void updatePatient_shouldRejectEmptyUpdate() {
        PatientResponseDto created =
                patientService.createPatient(validCreateRequest());

        assertThrows(
                InvalidUpdateRequestException.class,
                () -> patientService.updatePatient(
                        created.id(),
                        new UpdatePatientRequestDto(null, null, null)
                )
        );
    }

    @Test
    void updatePatient_shouldRejectNullRequest() {
        PatientResponseDto created =
                patientService.createPatient(validCreateRequest());

        assertThrows(
                InvalidUpdateRequestException.class,
                () -> patientService.updatePatient(created.id(), null)
        );
    }

    @Test
    void updatePatient_shouldPreserveAllFieldsWhenPhoneIsInvalid() {
        PatientResponseDto created =
                patientService.createPatient(validCreateRequest());

        assertThrows(
                InvalidPhoneNumberException.class,
                () -> patientService.updatePatient(
                        created.id(),
                        new UpdatePatientRequestDto(
                                "new@example.com",
                                "   ",
                                BloodType.A_POSITIVE
                        )
                )
        );

        Patient stored = patientRepository
                .findByUniqueId(created.id())
                .orElseThrow();

        assertAll(
                () -> assertEquals(
                        "deniz@example.com",
                        stored.getUser().getEmail()
                ),
                () -> assertEquals(
                        "05000000000",
                        stored.getUser().getPhoneNumber()
                ),
                () -> assertEquals(
                        BloodType.UNKNOWN,
                        stored.getBloodType()
                )
        );
    }

    private CreatePatientRequestDto validCreateRequest() {
        return new CreatePatientRequestDto(
                "10000000146",
                "Deniz",
                "Yilmaz",
                LocalDate.of(2000, 5, 10),
                "deniz@example.com",
                "05000000000"
        );
    }
}
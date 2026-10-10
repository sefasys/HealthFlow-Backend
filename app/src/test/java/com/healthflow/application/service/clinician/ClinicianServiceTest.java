package com.healthflow.application.service.clinician;

import com.healthflow.application.dto.clinician.*;
import com.healthflow.application.exception.ClinicianAlreadyExistsException;
import com.healthflow.application.exception.ClinicianNotFoundException;
import com.healthflow.application.exception.InvalidSearchQueryException;
import com.healthflow.application.exception.InvalidUpdateRequestException;
import com.healthflow.application.mapper.clinician.ClinicianMapper;
import com.healthflow.application.service.ClinicianService;
import com.healthflow.application.service.UserService;
import com.healthflow.domain.exception.IncompatibleUserRoleException;
import com.healthflow.domain.exception.InvalidPhoneNumberException;
import com.healthflow.domain.factory.StaffFactory;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.domain.model.user.staff.EmploymentStatus;
import com.healthflow.infrastructure.repository.InMemoryClinicianRepository;
import com.healthflow.infrastructure.repository.InMemoryUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClinicianServiceTest {

    private static final String NATIONAL_ID = "10000000146";
    private static final String EMAIL = "deniz@example.com";
    private static final String PHONE = "05000000000";

    private InMemoryClinicianRepository clinicianRepository;
    private InMemoryUserRepository userRepository;
    private UserFactory userFactory;
    private ClinicianService clinicianService;

    @BeforeEach
    void setUp() {
        clinicianRepository = new InMemoryClinicianRepository();
        userRepository = new InMemoryUserRepository();
        userFactory = new UserFactory();

        UserService userService = new UserService(
                userRepository,
                userFactory
        );

        ClinicianMapper clinicianMapper =
                Mappers.getMapper(ClinicianMapper.class);

        clinicianService = new ClinicianService(
                clinicianRepository,
                userService,
                clinicianMapper,
                new StaffFactory()
        );
    }

    @Test
    void createClinician_shouldSaveUserAndClinician() {
        CreateClinicianRequestDto request = validCreateRequest();

        ClinicianResponseDto response =
                clinicianService.createClinician(request);

        Clinician clinician = clinicianRepository
                .findByUniqueId(response.id())
                .orElseThrow();

        User user = userRepository
                .findByUniqueId(response.id())
                .orElseThrow();

        assertAll(
                () -> assertSame(user, clinician.getStaff().getUser()),
                () -> assertEquals(
                        new NationalId(NATIONAL_ID),
                        user.getNationalId()
                ),
                () -> assertEquals(
                        List.of(UserRole.CLINICIAN),
                        user.getUserRoleList()
                ),
                () -> assertEquals(request.name(), response.name()),
                () -> assertEquals(request.surname(), response.surname()),
                () -> assertEquals(
                        request.birthDate(),
                        response.birthDate()
                ),
                () -> assertEquals(request.hireDate(), response.hireDate()),
                () -> assertEquals(
                        request.employmentStatus(),
                        response.employmentStatus()
                ),
                () -> assertEquals(
                        1,
                        clinicianRepository.getClinicians().size()
                )
        );
    }

    @Test
    void createClinician_shouldReuseExistingPatientUser() {
        User existingUser = saveUserWithRole(UserRole.PATIENT);

        ClinicianResponseDto response =
                clinicianService.createClinician(validCreateRequest());

        User clinicianUser = clinicianRepository
                .findByUniqueId(response.id())
                .orElseThrow()
                .getStaff()
                .getUser();

        assertAll(
                () -> assertEquals(existingUser.getUniqueId(), response.id()),
                () -> assertSame(existingUser, clinicianUser),
                () -> assertTrue(
                        existingUser.getUserRoleList().contains(UserRole.PATIENT)
                ),
                () -> assertTrue(
                        existingUser.getUserRoleList().contains(UserRole.CLINICIAN)
                ),
                () -> assertEquals(2, existingUser.getUserRoleList().size()),
                () -> assertEquals("existing@example.com", clinicianUser.getEmail()),
                () -> assertEquals("05000000001", clinicianUser.getPhoneNumber())
        );
    }

    @Test
    void createClinician_shouldRejectExistingRegistrarRole() {
        User registrarUser = saveUserWithRole(UserRole.CLINIC_REGISTRAR);

        assertThrows(
                IncompatibleUserRoleException.class,
                () -> clinicianService.createClinician(validCreateRequest())
        );

        assertAll(
                () -> assertTrue(clinicianRepository.getClinicians().isEmpty()),
                () -> assertEquals(
                        List.of(UserRole.CLINIC_REGISTRAR),
                        registrarUser.getUserRoleList()
                ),
                () -> assertSame(
                        registrarUser,
                        userRepository.findByNationalId(
                                new NationalId(NATIONAL_ID)
                        ).orElseThrow()
                )
        );
    }

    @Test
    void createClinician_shouldRejectDuplicateNationalId() {
        ClinicianResponseDto first =
                clinicianService.createClinician(validCreateRequest());

        assertThrows(
                ClinicianAlreadyExistsException.class,
                () -> clinicianService.createClinician(validCreateRequest())
        );

        assertAll(
                () -> assertEquals(
                        1,
                        clinicianRepository.getClinicians().size()
                ),
                () -> assertEquals(
                        first.id(),
                        userRepository.findByNationalId(
                                new NationalId(NATIONAL_ID)
                        ).orElseThrow().getUniqueId()
                )
        );
    }

    @Test
    void findClinicianByUniqueId_shouldReturnClinician() {
        ClinicianResponseDto created =
                clinicianService.createClinician(validCreateRequest());

        ClinicianResponseDto found =
                clinicianService.findClinicianByUniqueId(created.id());

        assertEquals(created, found);
    }

    @Test
    void findClinicianByUniqueId_shouldRejectUnknownId() {
        assertThrows(
                ClinicianNotFoundException.class,
                () -> clinicianService.findClinicianByUniqueId(UUID.randomUUID())
        );
    }

    @Test
    void findClinicianByNationalId_shouldReturnClinician() {
        ClinicianResponseDto created =
                clinicianService.createClinician(validCreateRequest());

        ClinicianResponseDto found =
                clinicianService.findClinicianByNationalId(
                        new FindClinicianByNationalIdRequestDto(NATIONAL_ID)
                );

        assertEquals(created, found);
    }

    @Test
    void updateClinician_shouldUpdateOnlyEmail() {
        ClinicianResponseDto created =
                clinicianService.createClinician(validCreateRequest());

        UpdateClinicianResponseDto response =
                clinicianService.updateClinician(
                        created.id(),
                        new UpdateClinicianRequestDto(
                                "updated@example.com",
                                null
                        )
                );

        User savedUser = clinicianRepository
                .findByUniqueId(created.id())
                .orElseThrow()
                .getStaff()
                .getUser();

        assertAll(
                () -> assertEquals(created.id(), response.id()),
                () -> assertEquals("updated@example.com", response.email()),
                () -> assertEquals(PHONE, response.phoneNumber()),
                () -> assertEquals("updated@example.com", savedUser.getEmail()),
                () -> assertEquals(PHONE, savedUser.getPhoneNumber()),
                () -> assertEquals(
                        "updated@example.com",
                        userRepository.findByUniqueId(created.id())
                                .orElseThrow()
                                .getEmail()
                )
        );
    }

    @Test
    void updateClinician_shouldRejectEmptyRequest() {
        ClinicianResponseDto created =
                clinicianService.createClinician(validCreateRequest());

        assertThrows(
                InvalidUpdateRequestException.class,
                () -> clinicianService.updateClinician(
                        created.id(),
                        new UpdateClinicianRequestDto(null, null)
                )
        );
    }

    @Test
    void updateClinician_shouldRejectNullRequest() {
        ClinicianResponseDto created =
                clinicianService.createClinician(validCreateRequest());

        assertThrows(
                InvalidUpdateRequestException.class,
                () -> clinicianService.updateClinician(created.id(), null)
        );
    }

    @Test
    void updateClinician_shouldNotChangeEmailWhenPhoneIsInvalid() {
        ClinicianResponseDto created =
                clinicianService.createClinician(validCreateRequest());

        assertThrows(
                InvalidPhoneNumberException.class,
                () -> clinicianService.updateClinician(
                        created.id(),
                        new UpdateClinicianRequestDto(
                                "updated@example.com",
                                "   "
                        )
                )
        );

        User savedUser = clinicianRepository
                .findByUniqueId(created.id())
                .orElseThrow()
                .getStaff()
                .getUser();

        assertAll(
                () -> assertEquals(EMAIL, savedUser.getEmail()),
                () -> assertEquals(PHONE, savedUser.getPhoneNumber())
        );
    }

    @Test
    void searchClinician_shouldRejectBlankQuery() {
        assertThrows(
                InvalidSearchQueryException.class,
                () -> clinicianService.searchClinician("   ")
        );
    }

    private CreateClinicianRequestDto validCreateRequest() {
        return new CreateClinicianRequestDto(
                NATIONAL_ID,
                "Deniz",
                "Yilmaz",
                LocalDate.of(1990, 5, 10),
                EMAIL,
                PHONE,
                LocalDate.of(2024, 1, 15),
                EmploymentStatus.ACTIVE
        );
    }

    private User saveUserWithRole(UserRole role) {
        User user = userFactory.createUser(
                new NationalId(NATIONAL_ID),
                "Deniz",
                "Yilmaz",
                LocalDate.of(1990, 5, 10),
                "existing@example.com",
                "05000000001",
                role
        );

        return userRepository.save(user);
    }
}
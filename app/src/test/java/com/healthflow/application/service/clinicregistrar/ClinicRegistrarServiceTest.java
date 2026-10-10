package com.healthflow.application.service.clinicregistrar;

import com.healthflow.application.dto.clinicregistrar.*;
import com.healthflow.application.exception.ClinicRegistrarAlreadyExistsException;
import com.healthflow.application.exception.ClinicRegistrarNotFoundException;
import com.healthflow.application.exception.InvalidSearchQueryException;
import com.healthflow.application.exception.InvalidUpdateRequestException;
import com.healthflow.application.mapper.clinicregistrar.ClinicRegistrarMapper;
import com.healthflow.application.service.ClinicRegistrarService;
import com.healthflow.application.service.UserService;
import com.healthflow.domain.exception.IncompatibleUserRoleException;
import com.healthflow.domain.exception.InvalidPhoneNumberException;
import com.healthflow.domain.factory.StaffFactory;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.staff.ClinicRegistrar;
import com.healthflow.domain.model.user.staff.EmploymentStatus;
import com.healthflow.infrastructure.repository.InMemoryClinicRegistrarRepository;
import com.healthflow.infrastructure.repository.InMemoryUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClinicRegistrarServiceTest {

    private static final String NATIONAL_ID = "10000000146";
    private static final String EMAIL = "deniz@example.com";
    private static final String PHONE = "05000000000";

    private InMemoryClinicRegistrarRepository registrarRepository;
    private InMemoryUserRepository userRepository;
    private UserFactory userFactory;
    private ClinicRegistrarService registrarService;

    @BeforeEach
    void setUp() {
        registrarRepository = new InMemoryClinicRegistrarRepository();
        userRepository = new InMemoryUserRepository();
        userFactory = new UserFactory();

        UserService userService = new UserService(
                userRepository,
                userFactory
        );

        ClinicRegistrarMapper mapper =
                Mappers.getMapper(ClinicRegistrarMapper.class);

        registrarService = new ClinicRegistrarService(
                registrarRepository,
                userService,
                new StaffFactory(),
                mapper
        );
    }

    @Test
    void createClinicRegistrar_shouldSaveUserAndRegistrar() {
        CreateClinicRegistrarRequestDto request = validCreateRequest();

        ClinicRegistrarResponseDto response =
                registrarService.createClinicRegistrar(request);

        ClinicRegistrar registrar = registrarRepository
                .findByUniqueId(response.id())
                .orElseThrow();

        User user = userRepository
                .findByUniqueId(response.id())
                .orElseThrow();

        assertAll(
                () -> assertSame(user, registrar.getStaff().getUser()),
                () -> assertEquals(
                        new NationalId(NATIONAL_ID),
                        user.getNationalId()
                ),
                () -> assertEquals(
                        List.of(UserRole.CLINIC_REGISTRAR),
                        user.getUserRoleList()
                ),
                () -> assertEquals(request.name(), response.name()),
                () -> assertEquals(request.surname(), response.surname()),
                () -> assertEquals(
                        request.birthDate(),
                        response.birthDate()
                ),
                () -> assertEquals(
                        request.hireDate(),
                        response.hireDate()
                ),
                () -> assertEquals(
                        request.employmentStatus(),
                        response.employmentStatus()
                ),
                () -> assertEquals(
                        1,
                        registrarRepository.getClinicRegistrars().size()
                )
        );
    }

    @Test
    void createClinicRegistrar_shouldReuseExistingPatientUser() {
        User existingUser = saveUserWithRole(UserRole.PATIENT);

        ClinicRegistrarResponseDto response =
                registrarService.createClinicRegistrar(validCreateRequest());

        User registrarUser = registrarRepository
                .findByUniqueId(response.id())
                .orElseThrow()
                .getStaff()
                .getUser();

        assertAll(
                () -> assertEquals(existingUser.getUniqueId(), response.id()),
                () -> assertSame(existingUser, registrarUser),
                () -> assertTrue(
                        registrarUser.getUserRoleList().contains(UserRole.PATIENT)
                ),
                () -> assertTrue(
                        registrarUser.getUserRoleList()
                                .contains(UserRole.CLINIC_REGISTRAR)
                ),
                () -> assertEquals(2, registrarUser.getUserRoleList().size()),
                () -> assertEquals(
                        "existing@example.com",
                        registrarUser.getEmail()
                ),
                () -> assertEquals(
                        "05000000001",
                        registrarUser.getPhoneNumber()
                )
        );
    }

    @Test
    void createClinicRegistrar_shouldRejectExistingClinicianRole() {
        User clinicianUser = saveUserWithRole(UserRole.CLINICIAN);

        assertThrows(
                IncompatibleUserRoleException.class,
                () -> registrarService.createClinicRegistrar(
                        validCreateRequest()
                )
        );

        assertAll(
                () -> assertTrue(
                        registrarRepository.getClinicRegistrars().isEmpty()
                ),
                () -> assertEquals(
                        List.of(UserRole.CLINICIAN),
                        clinicianUser.getUserRoleList()
                ),
                () -> assertSame(
                        clinicianUser,
                        userRepository.findByNationalId(
                                new NationalId(NATIONAL_ID)
                        ).orElseThrow()
                )
        );
    }

    @Test
    void createClinicRegistrar_shouldRejectDuplicateNationalId() {
        ClinicRegistrarResponseDto first =
                registrarService.createClinicRegistrar(validCreateRequest());

        assertThrows(
                ClinicRegistrarAlreadyExistsException.class,
                () -> registrarService.createClinicRegistrar(
                        validCreateRequest()
                )
        );

        assertAll(
                () -> assertEquals(
                        1,
                        registrarRepository.getClinicRegistrars().size()
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
    void findClinicRegistrarByUniqueId_shouldReturnRegistrar() {
        ClinicRegistrarResponseDto created =
                registrarService.createClinicRegistrar(validCreateRequest());

        ClinicRegistrarResponseDto found =
                registrarService.findClinicRegistrarByUniqueId(created.id());

        assertEquals(created, found);
    }

    @Test
    void findClinicRegistrarByUniqueId_shouldRejectUnknownId() {
        assertThrows(
                ClinicRegistrarNotFoundException.class,
                () -> registrarService.findClinicRegistrarByUniqueId(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void findClinicRegistrarByNationalId_shouldReturnRegistrar() {
        ClinicRegistrarResponseDto created =
                registrarService.createClinicRegistrar(validCreateRequest());

        ClinicRegistrarResponseDto found =
                registrarService.findClinicRegistrarByNationalId(
                        new FindClinicRegistrarByNationalIdRequestDto(
                                NATIONAL_ID
                        )
                );

        assertEquals(created, found);
    }

    @Test
    void updateClinicRegistrar_shouldUpdateOnlyEmail() {
        ClinicRegistrarResponseDto created =
                registrarService.createClinicRegistrar(validCreateRequest());

        UpdateClinicRegistrarResponseDto response =
                registrarService.updateClinicRegistrar(
                        created.id(),
                        new UpdateClinicRegistrarRequestDto(
                                "updated@example.com",
                                null
                        )
                );

        User savedUser = registrarRepository
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
    void updateClinicRegistrar_shouldRejectEmptyRequest() {
        ClinicRegistrarResponseDto created =
                registrarService.createClinicRegistrar(validCreateRequest());

        assertThrows(
                InvalidUpdateRequestException.class,
                () -> registrarService.updateClinicRegistrar(
                        created.id(),
                        new UpdateClinicRegistrarRequestDto(null, null)
                )
        );
    }

    @Test
    void updateClinicRegistrar_shouldRejectNullRequest() {
        ClinicRegistrarResponseDto created =
                registrarService.createClinicRegistrar(validCreateRequest());

        assertThrows(
                InvalidUpdateRequestException.class,
                () -> registrarService.updateClinicRegistrar(
                        created.id(),
                        null
                )
        );
    }

    @Test
    void updateClinicRegistrar_shouldNotChangeEmailWhenPhoneIsInvalid() {
        ClinicRegistrarResponseDto created =
                registrarService.createClinicRegistrar(validCreateRequest());

        assertThrows(
                InvalidPhoneNumberException.class,
                () -> registrarService.updateClinicRegistrar(
                        created.id(),
                        new UpdateClinicRegistrarRequestDto(
                                "updated@example.com",
                                "   "
                        )
                )
        );

        User savedUser = registrarRepository
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
    void searchClinicRegistrar_shouldRejectBlankQuery() {
        assertThrows(
                InvalidSearchQueryException.class,
                () -> registrarService.searchClinicRegistrar("   ")
        );
    }

    private CreateClinicRegistrarRequestDto validCreateRequest() {
        return new CreateClinicRegistrarRequestDto(
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
package com.healthflow.application.service.user;

import com.healthflow.application.service.UserService;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.infrastructure.repository.InMemoryUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private InMemoryUserRepository userRepository;
    private UserFactory userFactory;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        userFactory = new UserFactory();

        userService = new UserService(
                userRepository,
                userFactory
        );
    }

    @Test
    void resolveUser_shouldCreateNewUser_whenUserDoesNotExist() {
        NationalId nationalId = new NationalId("10000000146");
        LocalDate birthDate = LocalDate.of(2000, 5, 10);

        User result = userService.resolveUser(
                nationalId,
                "Deniz",
                "Yilmaz",
                birthDate,
                "deniz@example.com",
                "05000000000",
                UserRole.PATIENT
        );

        assertAll(
                () -> assertNotNull(result.getUniqueId()),
                () -> assertEquals(nationalId, result.getNationalId()),
                () -> assertEquals("Deniz", result.getName()),
                () -> assertEquals("Yilmaz", result.getSurname()),
                () -> assertEquals(birthDate, result.getBirthDate()),
                () -> assertEquals(
                        "deniz@example.com",
                        result.getEmail()
                ),
                () -> assertEquals(
                        "05000000000",
                        result.getPhoneNumber()
                ),
                () -> assertEquals(
                        List.of(UserRole.PATIENT),
                        result.getUserRoleList()
                )
        );
    }

    @Test
    void resolveUser_shouldNotSaveNewUserAutomatically() {
        NationalId nationalId = new NationalId("10000000146");

        User result = userService.resolveUser(
                nationalId,
                "Deniz",
                "Yilmaz",
                LocalDate.of(2000, 5, 10),
                "deniz@example.com",
                "05000000000",
                UserRole.PATIENT
        );

        assertAll(
                () -> assertTrue(
                        userRepository.findByNationalId(nationalId).isEmpty()
                ),
                () -> assertTrue(
                        userRepository
                                .findByUniqueId(result.getUniqueId())
                                .isEmpty()
                )
        );
    }

    @Test
    void resolveUser_shouldReturnExistingUserWithoutChangingPersonalDetails() {
        User existingUser = createAndSavePatientUser();

        User result = userService.resolveUser(
                new NationalId("10000000146"),
                "DifferentName",
                "DifferentSurname",
                LocalDate.of(1995, 1, 1),
                "different@example.com",
                "05000000001",
                UserRole.CLINICIAN
        );

        assertAll(
                () -> assertSame(existingUser, result),
                () -> assertEquals("Deniz", result.getName()),
                () -> assertEquals("Yilmaz", result.getSurname()),
                () -> assertEquals(
                        LocalDate.of(2000, 5, 10),
                        result.getBirthDate()
                ),
                () -> assertEquals(
                        "deniz@example.com",
                        result.getEmail()
                ),
                () -> assertEquals(
                        "05000000000",
                        result.getPhoneNumber()
                )
        );
    }

    @Test
    void resolveUser_shouldNotAddRequestedRoleToExistingUser() {
        User existingUser = createAndSavePatientUser();

        User result = userService.resolveUser(
                new NationalId("10000000146"),
                "Deniz",
                "Yilmaz",
                LocalDate.of(2000, 5, 10),
                "deniz@example.com",
                "05000000000",
                UserRole.CLINICIAN
        );

        assertAll(
                () -> assertSame(existingUser, result),
                () -> assertEquals(
                        List.of(UserRole.PATIENT),
                        result.getUserRoleList()
                )
        );
    }

    @Test
    void saveUser_shouldPersistUserAndReturnSavedUser() {
        User user = userFactory.createUser(
                new NationalId("10000000146"),
                "Deniz",
                "Yilmaz",
                LocalDate.of(2000, 5, 10),
                "deniz@example.com",
                "05000000000",
                UserRole.PATIENT
        );

        User result = userService.saveUser(user);

        assertAll(
                () -> assertSame(user, result),
                () -> assertSame(
                        user,
                        userRepository
                                .findByUniqueId(user.getUniqueId())
                                .orElseThrow()
                ),
                () -> assertSame(
                        user,
                        userRepository
                                .findByNationalId(
                                        new NationalId("10000000146")
                                )
                                .orElseThrow()
                )
        );
    }

    private User createAndSavePatientUser() {
        User user = userFactory.createUser(
                new NationalId("10000000146"),
                "Deniz",
                "Yilmaz",
                LocalDate.of(2000, 5, 10),
                "deniz@example.com",
                "05000000000",
                UserRole.PATIENT
        );

        userRepository.save(user);
        return user;
    }
}
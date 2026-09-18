package com.healthflow.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.healthflow.application.exception.InvalidSearchQueryException;
import com.healthflow.application.exception.InvalidUpdateRequestException;
import com.healthflow.application.exception.PatientAlreadyExistsException;
import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.application.usecase.patient.*;
import com.healthflow.domain.exception.DomainValidationException;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.patient.BloodType;
import com.healthflow.domain.model.user.patient.Patient;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PatientController.class) // MOCK KULLANIYORUZ BURADA. DİKKAT ET BURADA MOCK'A.
class PatientControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockitoBean private CreatePatientUseCase createPatientUseCase;

  @MockitoBean private GetPatientsUseCase getPatientsUseCase;

  @MockitoBean private FindPatientByUniqueIdUseCase findPatientByUniqueIdUseCase;

  @MockitoBean
  private FindPatientByNationalIdUseCase findPatientByNationalIdUseCase;

  @MockitoBean
  private SearchPatientUseCase searchPatientUseCase;

  @MockitoBean
  private UpdatePatientUseCase updatePatientUseCase;

  // ==============================
  // POST /patients
  // Create Patient Tests
  // ==============================



  // 409 Conflict - Patient already exists
  @Test
  void shouldReturn409WhenPatientAlreadyExists() throws Exception {

    doThrow(new PatientAlreadyExistsException("Patient already exists"))
        .when(createPatientUseCase)
        .execute(any(), any(), any(), any(), any(), any());

    mockMvc
        .perform(
            post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                            {
                              "nationalId": "12345678910",
                              "name": "Sefa",
                              "surname": "Soysal",
                              "birthDate": "2004-01-01",
                              "email": "sefa@example.com",
                              "phoneNumber": "5555555555"
                            }
                            """))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.code").value("PATIENT_ALREADY_EXISTS"));
  }

    @Test
    void shouldReturn400WhenDomainValidationFails() throws Exception {

        when(createPatientUseCase.execute(
                any(NationalId.class),
                any(String.class),
                any(String.class),
                any(LocalDate.class),
                any(String.class),
                any(String.class)))
                .thenThrow(
                        new DomainValidationException("Domain validation failed.")
                );

        mockMvc.perform(
                        post("/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                    {
                      "nationalId": "12345678910",
                      "name": "Sefa",
                      "surname": "Soysal",
                      "birthDate": "2004-01-01",
                      "email": "sefa@example.com",
                      "phoneNumber": "5555555555"
                    }
                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("DOMAIN_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Domain validation failed."));
    }

  // 500 Internal Server Error - Unexpected server error
  @Test
  void shouldReturn500WhenUnexpectedExceptionOccurs() throws Exception {

    doThrow(new RuntimeException("Unexpected error"))
        .when(createPatientUseCase)
        .execute(any(), any(), any(), any(), any(), any());

    mockMvc
        .perform(
            post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                            {
                              "nationalId": "12345678910",
                              "name": "Sefa",
                              "surname": "Soysal",
                              "birthDate": "2004-01-01",
                              "email": "sefa@example.com",
                              "phoneNumber": "5555555555"
                            }
                            """))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
  }

  // 201 Created - Successful patient creation
  @Test
  void shouldReturn201WhenPatientCreatedSuccessfully() throws Exception {

    NationalId nationalId = new NationalId("12345678910");

    UserFactory userFactory = new UserFactory();

    User user =
        userFactory.createUser(
            nationalId,
            "Sefa",
            "Soysal",
            LocalDate.of(2004, 1, 1),
            "sefa@example.com",
            "5555555555",
            UserRole.PATIENT);

    Patient patient = new Patient(user);

    when(createPatientUseCase.execute(
            any(NationalId.class),
            any(String.class),
            any(String.class),
            any(LocalDate.class),
            any(String.class),
            any(String.class)))
        .thenReturn(patient);
    mockMvc
        .perform(
            post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                            {
                              "nationalId": "12345678910",
                              "name": "Sefa",
                              "surname": "Soysal",
                              "birthDate": "2004-01-01",
                              "email": "sefa@example.com",
                              "phoneNumber": "5555555555"
                            }
                            """))
        .andExpect(status().isCreated());
  }

  // ==============================
  // GET /patients
  // Get Patients Tests
  // ==============================

  // 200 OK - Returns patient list

  @Test
  void shouldReturn200WithPatients() throws Exception {

    UserFactory userFactory = new UserFactory();

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

    when(getPatientsUseCase.execute()).thenReturn(List.of(patient));

    mockMvc
        .perform(get("/patients").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Sefa"))
        .andExpect(jsonPath("$[0].surname").value("Soysal"));
  }

  // 500 Internal Server Error - Unexpected server error
  @Test
  void shouldReturn500WhenGetPatientsFails() throws Exception {

    doThrow(new RuntimeException("Unexpected error")).when(getPatientsUseCase).execute();

    mockMvc
        .perform(get("/patients").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
  }

  // 200 OK - Returns empty list when no patient exists
  @Test
  void shouldReturn200WithEmptyListWhenNoPatientsExist() throws Exception {

    when(getPatientsUseCase.execute()).thenReturn(List.of());

    mockMvc.perform(get("/patients")).andExpect(status().isOk()).andExpect(content().json("[]"));
  }

  // ==============================
  // GET /patients/{uniqueId}
  // Find Patient By Unique ID Tests
  // ==============================

  // 200 OK - Patient found

  @Test
  void shouldReturn200WhenPatientFoundByUniqueId() throws Exception {

    UUID uniqueId = UUID.randomUUID();

    UserFactory userFactory = new UserFactory();

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

    when(findPatientByUniqueIdUseCase.execute(uniqueId))
            .thenReturn(patient);

    mockMvc
        .perform(get("/patients/{uniqueId}", uniqueId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Sefa"))
        .andExpect(jsonPath("$.surname").value("Soysal"));
  }

  // 404 Not Found - Patient does not exist

  @Test
  void shouldReturn404WhenPatientNotFoundByUniqueId() throws Exception {

    UUID uniqueId = UUID.randomUUID();
    UserFactory userFactory = new UserFactory();

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

      doThrow(new PatientNotFoundException("Patient not found."))
              .when(findPatientByUniqueIdUseCase)
              .execute(uniqueId);

    mockMvc
        .perform(get("/patients/{uniqueId}", uniqueId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.code").value("PATIENT_NOT_FOUND"));
  }

  // 500 Internal Server Error - Unexpected server error
  @Test
  void shouldReturn500WhenFindPatientByUniqueIdFails() throws Exception {

    UUID uniqueId = UUID.randomUUID();

    doThrow(new RuntimeException("Unexpected error"))
        .when(findPatientByUniqueIdUseCase)
        .execute(uniqueId);

    mockMvc
        .perform(get("/patients/{uniqueId}", uniqueId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
  }

  // ==============================
// POST /patients/search-by-national-id
// Find Patient By National ID Tests
// ==============================

  @Test
  void shouldReturn200WhenPatientIsFoundByNationalId() throws Exception {

    NationalId nationalId = new NationalId("12345678910");
    UserFactory userFactory = new UserFactory();

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

    when(findPatientByNationalIdUseCase.execute(any(NationalId.class)))
            .thenReturn(patient);

    mockMvc.perform(
                    post("/patients/search-by-national-id")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                              "nationalId": "12345678910"
                            }
                            """)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Sefa"))
            .andExpect(jsonPath("$.surname").value("Soysal"))
            .andExpect(jsonPath("$.birthDate").value("2004-01-01"));
  }

  @Test
  void shouldReturn404WhenPatientIsNotFoundByNationalId() throws Exception {

    doThrow(new PatientNotFoundException("Patient not found."))
            .when(findPatientByNationalIdUseCase)
            .execute(any(NationalId.class));

    mockMvc.perform(
                    post("/patients/search-by-national-id")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                              "nationalId": "12345678910"
                            }
                            """)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("PATIENT_NOT_FOUND"));
  }


  @Test
  void shouldReturn500WhenFindByNationalIdFails() throws Exception {

    doThrow(new RuntimeException("Unexpected error"))
            .when(findPatientByNationalIdUseCase)
            .execute(any(NationalId.class));

    mockMvc.perform(
                    post("/patients/search-by-national-id")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                              "nationalId": "12345678910"
                            }
                            """)
            )
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
  }

// ==============================
// GET /patients/search
// Search Patients Tests
// ==============================

  @Test
  void shouldReturn200WithMatchingPatients() throws Exception {

    UserFactory userFactory = new UserFactory();

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

    when(searchPatientUseCase.execute("Sefa"))
            .thenReturn(List.of(patient));

    mockMvc.perform(
                    get("/patients/search")
                            .param("query", "Sefa")
                            .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Sefa"))
            .andExpect(jsonPath("$[0].surname").value("Soysal"));
  }


  @Test
  void shouldReturn200WithEmptyListWhenSearchHasNoMatches() throws Exception {

    when(searchPatientUseCase.execute("Unknown"))
            .thenReturn(List.of());

    mockMvc.perform(
                    get("/patients/search")
                            .param("query", "Unknown")
            )
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
  }

  @Test
  void shouldReturn400WhenSearchQueryIsInvalid() throws Exception {

    doThrow(new InvalidSearchQueryException(
            "Search query cannot be null or blank."
    ))
            .when(searchPatientUseCase)
            .execute("");

    mockMvc.perform(
                    get("/patients/search")
                            .param("query", "")
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("INVALID_SEARCH_QUERY"));
  }


  @Test
  void shouldReturn500WhenSearchFails() throws Exception {

    doThrow(new RuntimeException("Unexpected error"))
            .when(searchPatientUseCase)
            .execute("Sefa");

    mockMvc.perform(
                    get("/patients/search")
                            .param("query", "Sefa")
            )
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
  }

// ==============================
// PATCH /patients/{uniqueId}
// Update Patients Tests
// ==============================

@Test
void shouldReturn200WhenPatientUpdatedSuccessfully() throws Exception {

    UUID uniqueId = UUID.randomUUID();

    Patient patient = mock(Patient.class);

    User user = mock(User.class);

    when(patient.getUser()).thenReturn(user);

    when(user.getUniqueId()).thenReturn(uniqueId);
    when(user.getName()).thenReturn("Sefa");
    when(user.getSurname()).thenReturn("Soysal");
    when(user.getBirthDate()).thenReturn(LocalDate.of(2004, 1, 1));
    when(user.getEmail()).thenReturn("newmail@example.com");
    when(user.getPhoneNumber()).thenReturn("5555555555");

    when(updatePatientUseCase.execute(
            eq(uniqueId),
            eq("newmail@example.com"),
            eq("5555555555"),
            any(BloodType.class)))
            .thenReturn(patient);

    mockMvc.perform(
                    patch("/patients/{uniqueId}", uniqueId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                    {
                      "email": "newmail@example.com",
                      "phoneNumber": "5555555555",
                      "bloodType": "A_POSITIVE"
                    }
                    """))
            .andExpect(status().isOk());
}

    @Test
    void shouldReturn400WhenUpdateRequestIsInvalid() throws Exception { //FAILED

        UUID uniqueId = UUID.randomUUID();

        when(updatePatientUseCase.execute(
                eq(uniqueId),
                isNull(),
                isNull(),
                isNull()))
                .thenThrow(
                        new InvalidUpdateRequestException(
                                "At least one field must be provided for update."
                        )
                );

        mockMvc.perform(
                        patch("/patients/{uniqueId}", uniqueId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                    {}
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_UPDATE_REQUEST"));
    }

    @Test
    void shouldReturn404WhenUpdatingPatientNotFound() throws Exception {

        UUID uniqueId = UUID.randomUUID();

        when(updatePatientUseCase.execute(
                eq(uniqueId),
                eq("newmail@example.com"),
                isNull(),
                isNull()))
                .thenThrow(
                        new PatientNotFoundException("Patient could not be found.")
                );

        mockMvc.perform(
                        patch("/patients/{uniqueId}", uniqueId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                    {
                      "email": "newmail@example.com"
                    }
                    """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("PATIENT_NOT_FOUND"));
    }

    @Test
    void shouldReturn409WhenUpdateCausesConflict() throws Exception {

        UUID uniqueId = UUID.randomUUID();

        when(updatePatientUseCase.execute(
                eq(uniqueId),
                eq("existing@example.com"),
                isNull(),
                isNull()))
                .thenThrow(
                        new PatientAlreadyExistsException(
                                "Patient already exists."
                        )
                );

        mockMvc.perform(
                        patch("/patients/{uniqueId}", uniqueId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                    {
                      "email": "existing@example.com"
                    }
                    """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code").value("PATIENT_ALREADY_EXISTS"));
    }

    @Test
    void shouldReturn400WhenUpdateViolatesDomainValidation() throws Exception {

        UUID uniqueId = UUID.randomUUID();

        when(updatePatientUseCase.execute(
                eq(uniqueId),
                eq("invalid-email"),
                isNull(),
                isNull()))
                .thenThrow(
                        new DomainValidationException("Invalid email.")
                );

        mockMvc.perform(
                        patch("/patients/{uniqueId}", uniqueId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                    {
                      "email": "invalid-email"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("DOMAIN_VALIDATION_ERROR"));
    }

    @Test
    void shouldReturn500WhenUpdateFailsUnexpectedly() throws Exception {

        UUID uniqueId = UUID.randomUUID();

        when(updatePatientUseCase.execute(
                eq(uniqueId),
                eq("newmail@example.com"),
                isNull(),
                isNull()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(
                        patch("/patients/{uniqueId}", uniqueId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                    {
                      "email": "newmail@example.com"
                    }
                    """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
    }
}

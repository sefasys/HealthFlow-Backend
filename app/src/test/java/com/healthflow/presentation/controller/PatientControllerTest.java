package com.healthflow.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.healthflow.application.exception.PatientAlreadyExistsException;
import com.healthflow.application.usecase.patient.CreatePatientUseCase;
import com.healthflow.application.usecase.patient.FindPatientByNationalIdUseCase;
import com.healthflow.application.usecase.patient.FindPatientByUniqueIdUseCase;
import com.healthflow.application.usecase.patient.GetPatientsUseCase;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.patient.Patient;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

  // ==============================
  // POST /patients
  // Create Patient Tests
  // ==============================

  // 400 Bad Request - Invalid request validation
  @Test
  void shouldReturn400WhenRequestIsInvalid() throws Exception {

    mockMvc
        .perform(
            post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                            {
                              "nationalId": "",
                              "name": "",
                              "surname": "",
                              "birthDate": null,
                              "email": "invalid-email",
                              "phoneNumber": ""
                            }
                            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

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

    when(findPatientByUniqueIdUseCase.execute(uniqueId)).thenReturn(Optional.of(patient));

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

    when(findPatientByUniqueIdUseCase.execute(uniqueId)).thenReturn(Optional.empty());

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
    // GET /patients/search-by-national-id
    // Find Patient By National ID Tests
    // ==============================
    @Test
    void shouldReturn200WhenPatientFoundByNationalId() throws Exception {

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
                .thenReturn(Optional.of(patient));

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
                .andExpect(jsonPath("$.surname").value("Soysal"));
    }

    @Test
    void shouldReturn400WhenNationalIdRequestIsBlank() throws Exception {

        mockMvc.perform(
                        post("/patients/search-by-national-id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "nationalId": ""
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldReturn400WhenNationalIdIsInvalid() throws Exception {

        mockMvc.perform(
                        post("/patients/search-by-national-id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "nationalId": "123"
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_NATIONAL_ID"));
    }

    @Test
    void shouldReturn404WhenPatientNotFoundByNationalId() throws Exception {

        when(findPatientByNationalIdUseCase.execute(any(NationalId.class)))
                .thenReturn(Optional.empty());

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
    void shouldReturn500WhenFindPatientByNationalIdFails() throws Exception {

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
}

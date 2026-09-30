package com.healthflow.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.healthflow.presentation.dto.patient.CreatePatientRequestDto;
import com.healthflow.presentation.dto.patient.PatientResponseDto;
import com.healthflow.application.exception.InvalidSearchQueryException;
import com.healthflow.application.exception.InvalidUpdateRequestException;
import com.healthflow.application.exception.PatientAlreadyExistsException;
import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.application.service.PatientService;
import com.healthflow.domain.exception.DomainValidationException;
import com.healthflow.domain.model.user.patient.BloodType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Controller katmanı testi. Sadece HTTP davranışını (status kodu, JSON formatı, validasyon,
 * exception handler) doğrular. İş mantığı PatientService testlerinde test edilir, burada service
 * mock'lanır.
 */
@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private PatientService patientService;

    private static final String VALID_CREATE_REQUEST =
            """
            {
              "nationalId": "12345678910",
              "name": "Sefa",
              "surname": "Soysal",
              "birthDate": "2004-01-01",
              "email": "sefa@example.com",
              "phoneNumber": "5555555555"
            }
            """;

    private static final String NATIONAL_ID_REQUEST =
            """
            {
              "nationalId": "12345678910"
            }
            """;

    // Response DTO'nun constructor'ı değişirse düzeltilecek tek yer burası.
    private PatientResponseDto samplePatientResponse(UUID id) {
        return new PatientResponseDto(id, "Sefa", "Soysal", LocalDate.of(2004, 1, 1));
    }

    // ==============================
    // POST /patients
    // Create Patient Tests
    // ==============================

    @Test
    void shouldReturn201WhenPatientCreatedSuccessfully() throws Exception {

        when(patientService.createPatient(any(CreatePatientRequestDto.class)))
                .thenReturn(samplePatientResponse(UUID.randomUUID()));

        mockMvc
                .perform(
                        post("/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VALID_CREATE_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sefa"))
                .andExpect(jsonPath("$.surname").value("Soysal"))
                .andExpect(jsonPath("$.birthDate").value("2004-01-01"));

        verify(patientService).createPatient(any(CreatePatientRequestDto.class));
    }

    @Test
    void shouldReturn400WhenCreateRequestBodyIsInvalid() throws Exception {

        mockMvc
                .perform(post("/patients").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(patientService);
    }

    @Test
    void shouldReturn409WhenPatientAlreadyExists() throws Exception {

        when(patientService.createPatient(any(CreatePatientRequestDto.class)))
                .thenThrow(new PatientAlreadyExistsException("Patient already exists"));

        mockMvc
                .perform(
                        post("/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VALID_CREATE_REQUEST))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code").value("PATIENT_ALREADY_EXISTS"));
    }

    @Test
    void shouldReturn400WhenDomainValidationFails() throws Exception {

        when(patientService.createPatient(any(CreatePatientRequestDto.class)))
                .thenThrow(new DomainValidationException("Domain validation failed."));

        mockMvc
                .perform(
                        post("/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VALID_CREATE_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("DOMAIN_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Domain validation failed."));
    }

    @Test
    void shouldReturn500WhenCreatePatientFailsUnexpectedly() throws Exception {

        when(patientService.createPatient(any(CreatePatientRequestDto.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc
                .perform(
                        post("/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VALID_CREATE_REQUEST))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
    }

    // ==============================
    // GET /patients
    // Get Patients Tests
    // ==============================

    @Test
    void shouldReturn200WithPatients() throws Exception {

        when(patientService.getPatients())
                .thenReturn(List.of(samplePatientResponse(UUID.randomUUID())));

        mockMvc
                .perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sefa"))
                .andExpect(jsonPath("$[0].surname").value("Soysal"));
    }

    @Test
    void shouldReturn200WithEmptyListWhenNoPatientsExist() throws Exception {

        when(patientService.getPatients()).thenReturn(List.of());

        mockMvc.perform(get("/patients")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void shouldReturn500WhenGetPatientsFails() throws Exception {

        when(patientService.getPatients()).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc
                .perform(get("/patients"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
    }

    // ==============================
    // GET /patients/{uniqueId}
    // Find Patient By Unique ID Tests
    // ==============================

    @Test
    void shouldReturn200WhenPatientFoundByUniqueId() throws Exception {

        UUID uniqueId = UUID.randomUUID();

        when(patientService.findPatientByUniqueId(uniqueId))
                .thenReturn(samplePatientResponse(uniqueId));

        mockMvc
                .perform(get("/patients/{uniqueId}", uniqueId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sefa"))
                .andExpect(jsonPath("$.surname").value("Soysal"));
    }

    @Test
    void shouldReturn400WhenUniqueIdIsNotAValidUuid() throws Exception {

        mockMvc.perform(get("/patients/{uniqueId}", "not-a-uuid")).andExpect(status().isBadRequest());

        verifyNoInteractions(patientService);
    }

    @Test
    void shouldReturn404WhenPatientNotFoundByUniqueId() throws Exception {

        UUID uniqueId = UUID.randomUUID();

        when(patientService.findPatientByUniqueId(uniqueId))
                .thenThrow(new PatientNotFoundException("Patient not found."));

        mockMvc
                .perform(get("/patients/{uniqueId}", uniqueId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("PATIENT_NOT_FOUND"));
    }

    @Test
    void shouldReturn500WhenFindPatientByUniqueIdFails() throws Exception {

        UUID uniqueId = UUID.randomUUID();

        when(patientService.findPatientByUniqueId(uniqueId))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc
                .perform(get("/patients/{uniqueId}", uniqueId))
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

        when(patientService.findPatientByNationalId("12345678910"))
                .thenReturn(samplePatientResponse(UUID.randomUUID()));

        mockMvc
                .perform(
                        post("/patients/search-by-national-id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(NATIONAL_ID_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sefa"))
                .andExpect(jsonPath("$.surname").value("Soysal"))
                .andExpect(jsonPath("$.birthDate").value("2004-01-01"));
    }

    @Test
    void shouldReturn400WhenNationalIdRequestBodyIsInvalid() throws Exception {

        mockMvc
                .perform(
                        post("/patients/search-by-national-id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(patientService);
    }

    @Test
    void shouldReturn404WhenPatientIsNotFoundByNationalId() throws Exception {

        when(patientService.findPatientByNationalId("12345678910"))
                .thenThrow(new PatientNotFoundException("Patient not found."));

        mockMvc
                .perform(
                        post("/patients/search-by-national-id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(NATIONAL_ID_REQUEST))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("PATIENT_NOT_FOUND"));
    }

    @Test
    void shouldReturn500WhenFindByNationalIdFails() throws Exception {

        when(patientService.findPatientByNationalId("12345678910"))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc
                .perform(
                        post("/patients/search-by-national-id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(NATIONAL_ID_REQUEST))
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

        when(patientService.searchPatient("Sefa"))
                .thenReturn(List.of(samplePatientResponse(UUID.randomUUID())));

        mockMvc
                .perform(get("/patients/search").param("query", "Sefa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sefa"))
                .andExpect(jsonPath("$[0].surname").value("Soysal"));
    }

    @Test
    void shouldReturn200WithEmptyListWhenSearchHasNoMatches() throws Exception {

        when(patientService.searchPatient("Unknown")).thenReturn(List.of());

        mockMvc
                .perform(get("/patients/search").param("query", "Unknown"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldReturn400WhenSearchQueryIsInvalid() throws Exception {

        when(patientService.searchPatient(""))
                .thenThrow(new InvalidSearchQueryException("Search query cannot be null or blank."));

        mockMvc
                .perform(get("/patients/search").param("query", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_SEARCH_QUERY"));
    }

    @Test
    void shouldReturn400WhenSearchQueryParamIsMissing() throws Exception {

        mockMvc.perform(get("/patients/search")).andExpect(status().isBadRequest());

        verifyNoInteractions(patientService);
    }

    @Test
    void shouldReturn500WhenSearchFails() throws Exception {

        when(patientService.searchPatient("Sefa")).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc
                .perform(get("/patients/search").param("query", "Sefa"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
    }

    // ==============================
    // PATCH /patients/{uniqueId}
    // Update Patient Tests
    // ==============================

    @Test
    void shouldReturn200WhenPatientUpdatedSuccessfully() throws Exception {

        UUID uniqueId = UUID.randomUUID();

        when(patientService.updatePatient(eq(uniqueId), any()))
                .thenReturn(samplePatientResponse(uniqueId));

        mockMvc
                .perform(
                        patch("/patients/{uniqueId}", uniqueId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "email": "newmail@example.com",
                                          "phoneNumber": "5555555555",
                                          "bloodType": "A_POSITIVE"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sefa"));
    }

    @Test
    void shouldReturn400WhenUpdateRequestIsInvalid() throws Exception {

        UUID uniqueId = UUID.randomUUID();

        when(patientService.updatePatient(eq(uniqueId), any()))
                .thenThrow(
                        new InvalidUpdateRequestException("At least one field must be provided for update."));

        mockMvc
                .perform(patch("/patients/{uniqueId}", uniqueId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_UPDATE_REQUEST"));
    }

    @Test
    void shouldReturn404WhenUpdatingPatientNotFound() throws Exception {

        UUID uniqueId = UUID.randomUUID();

        when(patientService.updatePatient(eq(uniqueId), any()))
                .thenThrow(new PatientNotFoundException("Patient could not be found."));

        mockMvc
                .perform(
                        patch("/patients/{uniqueId}", uniqueId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
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

        when(patientService.updatePatient(eq(uniqueId), any()))
                .thenThrow(new PatientAlreadyExistsException("Patient already exists."));

        mockMvc
                .perform(
                        patch("/patients/{uniqueId}", uniqueId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
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

        when(patientService.updatePatient(eq(uniqueId), any()))
                .thenThrow(new DomainValidationException("Invalid email."));

        mockMvc
                .perform(
                        patch("/patients/{uniqueId}", uniqueId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
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

        when(patientService.updatePatient(eq(uniqueId), any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc
                .perform(
                        patch("/patients/{uniqueId}", uniqueId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "email": "newmail@example.com"
                                        }
                                        """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
    }
}
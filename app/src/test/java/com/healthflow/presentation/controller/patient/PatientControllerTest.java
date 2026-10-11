package com.healthflow.presentation.controller;

import com.healthflow.application.dto.patient.*;
import com.healthflow.application.exception.InvalidUpdateRequestException;
import com.healthflow.application.exception.PatientAlreadyExistsException;
import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.application.service.PatientService;
import com.healthflow.domain.exception.InvalidNationalIdException;
import com.healthflow.domain.model.user.patient.BloodType;
import com.healthflow.presentation.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PatientControllerTest {

    private static final String BASE_URL = "/api/patients";

    private static final UUID PATIENT_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final String VALID_CREATE_JSON = """
            {
              "nationalId": "10000000146",
              "name": "Deniz",
              "surname": "Yilmaz",
              "birthDate": "1990-05-10",
              "email": "deniz@example.com",
              "phoneNumber": "05000000000"
            }
            """;

    private PatientService patientService;
    private MockMvc mockMvc;
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setUp() {
        patientService = mock(PatientService.class);

        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new PatientController(patientService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void tearDown() {
        validator.close();
    }

    @Test
    void createPatient_shouldReturn201AndResponseBody() throws Exception {
        when(patientService.createPatient(any(CreatePatientRequestDto.class)))
                .thenReturn(patientResponse());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.id").value(PATIENT_ID.toString()))
                .andExpect(jsonPath("$.name").value("Deniz"))
                .andExpect(jsonPath("$.surname").value("Yilmaz"))
                .andExpect(jsonPath("$.birthDate").value("1990-05-10"));

        verify(patientService).createPatient(
                new CreatePatientRequestDto(
                        "10000000146",
                        "Deniz",
                        "Yilmaz",
                        LocalDate.of(1990, 5, 10),
                        "deniz@example.com",
                        "05000000000"
                )
        );
    }

    @Test
    void createPatient_shouldReturn400ForMissingRequiredFields()
            throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(patientService);
    }

    @Test
    void createPatient_shouldReturn400ForInvalidEmail() throws Exception {
        String invalidJson = VALID_CREATE_JSON.replace(
                "deniz@example.com",
                "invalid-email"
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(patientService);
    }

    @Test
    void createPatient_shouldReturn409ForDuplicatePatient() throws Exception {
        when(patientService.createPatient(any(CreatePatientRequestDto.class)))
                .thenThrow(new PatientAlreadyExistsException(
                        "Patient already exists."
                ));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code").value("RESOURCE_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value(
                        "Patient already exists."
                ));
    }

    @Test
    void createPatient_shouldReturn400ForDomainValidationError()
            throws Exception {

        when(patientService.createPatient(any(CreatePatientRequestDto.class)))
                .thenThrow(new InvalidNationalIdException(
                        "National ID is invalid."
                ));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DOMAIN_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value(
                        "National ID is invalid."
                ));
    }

    @Test
    void getPatients_shouldReturn200AndPatientList() throws Exception {
        when(patientService.getPatients())
                .thenReturn(List.of(patientResponse()));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(PATIENT_ID.toString()));

        verify(patientService).getPatients();
    }

    @Test
    void findPatientByUniqueId_shouldReturn200() throws Exception {
        when(patientService.findPatientByUniqueId(PATIENT_ID))
                .thenReturn(patientResponse());

        mockMvc.perform(get(BASE_URL + "/{uniqueId}", PATIENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PATIENT_ID.toString()));

        verify(patientService).findPatientByUniqueId(PATIENT_ID);
    }

    @Test
    void findPatientByUniqueId_shouldReturn404ForUnknownPatient()
            throws Exception {

        when(patientService.findPatientByUniqueId(PATIENT_ID))
                .thenThrow(new PatientNotFoundException(
                        "Patient could not be found."
                ));

        mockMvc.perform(get(BASE_URL + "/{uniqueId}", PATIENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(
                        "Patient could not be found."
                ));
    }

    @Test
    void findPatientByUniqueId_shouldReturn400ForInvalidUuid()
            throws Exception {

        mockMvc.perform(get(BASE_URL + "/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(patientService);
    }

    @Test
    void findPatientByNationalId_shouldForwardRequestAndReturn200()
            throws Exception {

        FindPatientByNationalIdRequestDto request =
                new FindPatientByNationalIdRequestDto("10000000146");

        when(patientService.findPatientByNationalId(request))
                .thenReturn(patientResponse());

        mockMvc.perform(post(BASE_URL + "/search-by-national-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nationalId": "10000000146"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PATIENT_ID.toString()));

        verify(patientService).findPatientByNationalId(request);
    }

    @Test
    void searchPatients_shouldForwardQueryAndReturn200() throws Exception {
        when(patientService.searchPatient("Deniz"))
                .thenReturn(List.of(patientResponse()));

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", "Deniz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Deniz"));

        verify(patientService).searchPatient("Deniz");
    }

    @Test
    void searchPatients_shouldReturn400WhenQueryIsMissing()
            throws Exception {

        mockMvc.perform(get(BASE_URL + "/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(patientService);
    }

    @Test
    void updatePatient_shouldAcceptEmailOnlyAndReturn200() throws Exception {
        UpdatePatientRequestDto request = new UpdatePatientRequestDto(
                "updated@example.com",
                null,
                null
        );

        when(patientService.updatePatient(PATIENT_ID, request))
                .thenReturn(new UpdatePatientResponseDto(
                        PATIENT_ID,
                        "Deniz",
                        "Yilmaz",
                        LocalDate.of(1990, 5, 10),
                        "updated@example.com",
                        "05000000000",
                        BloodType.UNKNOWN
                ));

        mockMvc.perform(patch(BASE_URL + "/{uniqueId}", PATIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "updated@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PATIENT_ID.toString()))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("05000000000"))
                .andExpect(jsonPath("$.bloodType").value("UNKNOWN"));

        verify(patientService).updatePatient(PATIENT_ID, request);
    }

    @Test
    void updatePatient_shouldReturn400ForBlankEmail() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/{uniqueId}", PATIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "   "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(patientService);
    }

    @Test
    void updatePatient_shouldMapEmptyUpdateExceptionTo400()
            throws Exception {

        UpdatePatientRequestDto emptyRequest =
                new UpdatePatientRequestDto(null, null, null);

        when(patientService.updatePatient(PATIENT_ID, emptyRequest))
                .thenThrow(new InvalidUpdateRequestException(
                        "At least one field must be provided for update."
                ));

        mockMvc.perform(patch(BASE_URL + "/{uniqueId}", PATIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(
                        "At least one field must be provided for update."
                ));

        verify(patientService).updatePatient(PATIENT_ID, emptyRequest);
    }

    @Test
    void createPatient_shouldReturn400ForMalformedJson() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(patientService);
    }

    @Test
    void getPatients_shouldReturnGeneric500ForUnexpectedError()
            throws Exception {

        when(patientService.getPatients())
                .thenThrow(new RuntimeException("Internal technical details"));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value(
                        "An unexpected error occurred."
                ));
    }

    private PatientResponseDto patientResponse() {
        return new PatientResponseDto(
                PATIENT_ID,
                "Deniz",
                "Yilmaz",
                LocalDate.of(1990, 5, 10)
        );
    }
}
package com.healthflow.presentation.controller.clinician;

import com.healthflow.application.dto.clinician.*;
import com.healthflow.application.exception.ClinicianAlreadyExistsException;
import com.healthflow.application.exception.ClinicianNotFoundException;
import com.healthflow.application.exception.InvalidSearchQueryException;
import com.healthflow.application.exception.InvalidUpdateRequestException;
import com.healthflow.application.service.ClinicianService;
import com.healthflow.domain.model.user.staff.EmploymentStatus;
import com.healthflow.presentation.controller.ClinicianController;
import com.healthflow.presentation.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClinicianController.class)
@Import(GlobalExceptionHandler.class)
class ClinicianControllerTest {

    private static final String BASE_URL = "/api/clinician";

    private static final UUID CLINICIAN_ID =
            UUID.fromString("11111111-1111-4111-8111-111111111111");

    private static final String NATIONAL_ID = "10000000146";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClinicianService clinicianService;

    // --------------------------------------------------
    // CREATE
    // --------------------------------------------------

    @Test
    void createClinician_shouldReturn201_whenRequestIsValid()
            throws Exception {

        CreateClinicianRequestDto expectedRequest =
                new CreateClinicianRequestDto(
                        NATIONAL_ID,
                        "Sefa",
                        "Soysal",
                        LocalDate.of(1990, 5, 15),
                        "sefa@example.com",
                        "05551234567",
                        LocalDate.of(2020, 1, 10),
                        EmploymentStatus.ACTIVE
                );

        when(clinicianService.createClinician(expectedRequest))
                .thenReturn(clinicianResponse());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id")
                        .value(CLINICIAN_ID.toString()))
                .andExpect(jsonPath("$.name").value("Sefa"))
                .andExpect(jsonPath("$.surname").value("Soysal"))
                .andExpect(jsonPath("$.birthDate").value("1990-05-15"))
                .andExpect(jsonPath("$.hireDate").value("2020-01-10"))
                .andExpect(jsonPath("$.employmentStatus").value("ACTIVE"));

        verify(clinicianService).createClinician(expectedRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "nationalId", "name", "surname", "email", "phoneNumber"
    })
    void createClinician_shouldReturn400_whenRequiredTextIsBlank(
            String field
    ) throws Exception {

        String json = validCreateJson().replaceAll(
                "\"" + field + "\"\\s*:\\s*\"[^\"]*\"",
                "\"" + field + "\": \" \""
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicianService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "birthDate", "hireDate", "employmentStatus"
    })
    void createClinician_shouldReturn400_whenRequiredFieldIsNull(
            String field
    ) throws Exception {

        String json = validCreateJson().replaceAll(
                "\"" + field + "\"\\s*:\\s*\"[^\"]*\"",
                "\"" + field + "\": null"
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicianService);
    }

    @Test
    void createClinician_shouldReturn400_whenEmailIsInvalid()
            throws Exception {

        String json = validCreateJson()
                .replace("sefa@example.com", "invalid-email");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(clinicianService);
    }

    @Test
    void createClinician_shouldReturn400_whenBirthDateIsInFuture()
            throws Exception {

        String json = validCreateJson().replace(
                "1990-05-15",
                LocalDate.now().plusYears(1).toString()
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(clinicianService);
    }

    @Test
    void createClinician_shouldReturn400_whenEmploymentStatusIsUnknown()
            throws Exception {

        String json = validCreateJson()
                .replace("\"ACTIVE\"", "\"UNKNOWN_STATUS\"");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicianService);
    }

    @Test
    void createClinician_shouldReturn409_whenClinicianAlreadyExists()
            throws Exception {

        when(clinicianService.createClinician(
                any(CreateClinicianRequestDto.class)
        )).thenThrow(new ClinicianAlreadyExistsException(
                "There is already a clinician with the same national ID."
        ));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code")
                        .value("RESOURCE_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value(
                        "There is already a clinician with the same national ID."
                ))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createClinician_shouldReturn400_whenJsonIsMalformed()
            throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicianService);
    }

    // --------------------------------------------------
    // GET ALL
    // --------------------------------------------------

    @Test
    void getClinicians_shouldReturn200AndList()
            throws Exception {

        when(clinicianService.getClinicians())
                .thenReturn(List.of(clinicianResponse()));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(CLINICIAN_ID.toString()))
                .andExpect(jsonPath("$[0].name").value("Sefa"));

        verify(clinicianService).getClinicians();
    }

    @Test
    void getClinicians_shouldReturn200AndEmptyArray_whenNoCliniciansExist()
            throws Exception {

        when(clinicianService.getClinicians())
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(clinicianService).getClinicians();
    }

    @Test
    void getClinicians_shouldReturn500_whenUnexpectedErrorOccurs()
            throws Exception {

        when(clinicianService.getClinicians())
                .thenThrow(new RuntimeException("Internal database detail"));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("An unexpected error occurred."));
    }

    // --------------------------------------------------
    // FIND BY UNIQUE ID
    // --------------------------------------------------

    @Test
    void findClinicianByUniqueId_shouldReturn200_whenClinicianExists()
            throws Exception {

        when(clinicianService.findClinicianByUniqueId(CLINICIAN_ID))
                .thenReturn(clinicianResponse());

        mockMvc.perform(get(
                        BASE_URL + "/find-by-unique-id/{uniqueId}",
                        CLINICIAN_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(CLINICIAN_ID.toString()))
                .andExpect(jsonPath("$.name").value("Sefa"));

        verify(clinicianService).findClinicianByUniqueId(CLINICIAN_ID);
    }

    @Test
    void findClinicianByUniqueId_shouldReturn404_whenClinicianDoesNotExist()
            throws Exception {

        when(clinicianService.findClinicianByUniqueId(CLINICIAN_ID))
                .thenThrow(new ClinicianNotFoundException(
                        "Clinician not found."
                ));

        mockMvc.perform(get(
                        BASE_URL + "/find-by-unique-id/{uniqueId}",
                        CLINICIAN_ID
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Clinician not found."));
    }

    @Test
    void findClinicianByUniqueId_shouldReturn400_whenUuidIsInvalid()
            throws Exception {

        mockMvc.perform(get(
                        BASE_URL + "/find-by-unique-id/{uniqueId}",
                        "invalid-uuid"
                ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicianService);
    }

    // --------------------------------------------------
    // FIND BY NATIONAL ID
    // --------------------------------------------------

    @Test
    void findClinicianByNationalId_shouldReturn200_whenClinicianExists()
            throws Exception {

        FindClinicianByNationalIdRequestDto expectedRequest =
                new FindClinicianByNationalIdRequestDto(NATIONAL_ID);

        when(clinicianService.findClinicianByNationalId(expectedRequest))
                .thenReturn(clinicianResponse());

        mockMvc.perform(post(BASE_URL + "/find-by-national-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nationalId": "10000000146"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(CLINICIAN_ID.toString()));

        verify(clinicianService).findClinicianByNationalId(expectedRequest);
    }

    @Test
    void findClinicianByNationalId_shouldReturn400_whenNationalIdIsBlank()
            throws Exception {

        mockMvc.perform(post(BASE_URL + "/find-by-national-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nationalId": " "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicianService);
    }

    @Test
    void findClinicianByNationalId_shouldReturn404_whenClinicianDoesNotExist()
            throws Exception {

        FindClinicianByNationalIdRequestDto expectedRequest =
                new FindClinicianByNationalIdRequestDto(NATIONAL_ID);

        when(clinicianService.findClinicianByNationalId(expectedRequest))
                .thenThrow(new ClinicianNotFoundException(
                        "Clinician not found with this national id."
                ));

        mockMvc.perform(post(BASE_URL + "/find-by-national-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nationalId": "10000000146"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    // --------------------------------------------------
    // UPDATE
    // --------------------------------------------------

    @Test
    void updateClinician_shouldReturn200_whenRequestIsValid()
            throws Exception {

        UpdateClinicianRequestDto expectedRequest =
                new UpdateClinicianRequestDto(
                        "new@example.com",
                        "05559876543"
                );

        UpdateClinicianResponseDto response =
                new UpdateClinicianResponseDto(
                        CLINICIAN_ID,
                        "Sefa",
                        "Soysal",
                        LocalDate.of(1990, 5, 15),
                        "new@example.com",
                        "05559876543"
                );

        when(clinicianService.updateClinician(
                CLINICIAN_ID, expectedRequest
        )).thenReturn(response);

        mockMvc.perform(patch(
                        BASE_URL + "/update/{uniqueId}",
                        CLINICIAN_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "new@example.com",
                                  "phoneNumber": "05559876543"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(CLINICIAN_ID.toString()))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("05559876543"));

        verify(clinicianService)
                .updateClinician(CLINICIAN_ID, expectedRequest);
    }

    @Test
    void updateClinician_shouldReturn200_whenOnlyEmailIsProvided()
            throws Exception {

        UpdateClinicianRequestDto expectedRequest =
                new UpdateClinicianRequestDto("new@example.com", null);

        UpdateClinicianResponseDto response =
                new UpdateClinicianResponseDto(
                        CLINICIAN_ID,
                        "Sefa",
                        "Soysal",
                        LocalDate.of(1990, 5, 15),
                        "new@example.com",
                        "05551234567"
                );

        when(clinicianService.updateClinician(
                CLINICIAN_ID, expectedRequest
        )).thenReturn(response);

        mockMvc.perform(patch(
                        BASE_URL + "/update/{uniqueId}",
                        CLINICIAN_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "new@example.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("05551234567"));

        verify(clinicianService)
                .updateClinician(CLINICIAN_ID, expectedRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"email\": \"invalid-email\"}",
            "{\"email\": \" \"}",
            "{\"phoneNumber\": \" \"}"
    })
    void updateClinician_shouldReturn400_whenContactDataIsInvalid(
            String json
    ) throws Exception {

        mockMvc.perform(patch(
                        BASE_URL + "/update/{uniqueId}",
                        CLINICIAN_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicianService);
    }

    @Test
    void updateClinician_shouldReturn400_whenNoFieldsAreProvided()
            throws Exception {

        UpdateClinicianRequestDto expectedRequest =
                new UpdateClinicianRequestDto(null, null);

        when(clinicianService.updateClinician(
                CLINICIAN_ID, expectedRequest
        )).thenThrow(new InvalidUpdateRequestException(
                "At least one field must be provided for update."
        ));

        mockMvc.perform(patch(
                        BASE_URL + "/update/{uniqueId}",
                        CLINICIAN_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(
                        "At least one field must be provided for update."
                ));

        verify(clinicianService)
                .updateClinician(CLINICIAN_ID, expectedRequest);
    }

    @Test
    void updateClinician_shouldReturn404_whenClinicianDoesNotExist()
            throws Exception {

        UpdateClinicianRequestDto expectedRequest =
                new UpdateClinicianRequestDto("new@example.com", null);

        when(clinicianService.updateClinician(
                CLINICIAN_ID, expectedRequest
        )).thenThrow(new ClinicianNotFoundException(
                "Clinician could not be found."
        ));

        mockMvc.perform(patch(
                        BASE_URL + "/update/{uniqueId}",
                        CLINICIAN_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "new@example.com"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    // --------------------------------------------------
    // SEARCH
    // --------------------------------------------------

    @Test
    void searchClinician_shouldReturn200AndMatchingClinicians()
            throws Exception {

        when(clinicianService.searchClinician("Sefa"))
                .thenReturn(List.of(clinicianResponse()));

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", "Sefa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(CLINICIAN_ID.toString()))
                .andExpect(jsonPath("$[0].name").value("Sefa"));

        verify(clinicianService).searchClinician("Sefa");
    }

    @Test
    void searchClinician_shouldReturn200AndEmptyArray_whenNoMatchExists()
            throws Exception {

        when(clinicianService.searchClinician("NoMatch"))
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", "NoMatch"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(clinicianService).searchClinician("NoMatch");
    }

    @Test
    void searchClinician_shouldReturn400_whenQueryParameterIsMissing()
            throws Exception {

        mockMvc.perform(get(BASE_URL + "/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicianService);
    }

    @Test
    void searchClinician_shouldReturn400_whenQueryIsBlank()
            throws Exception {

        when(clinicianService.searchClinician(" "))
                .thenThrow(new InvalidSearchQueryException(
                        "Search query cannot be null or blank."
                ));

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(
                        "Search query cannot be null or blank."
                ));

        verify(clinicianService).searchClinician(" ");
    }

    // --------------------------------------------------
    // TEST DATA
    // --------------------------------------------------

    private ClinicianResponseDto clinicianResponse() {
        return new ClinicianResponseDto(
                CLINICIAN_ID,
                "Sefa",
                "Soysal",
                LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10),
                EmploymentStatus.ACTIVE
        );
    }

    private String validCreateJson() {
        return """
                {
                  "nationalId": "10000000146",
                  "name": "Sefa",
                  "surname": "Soysal",
                  "birthDate": "1990-05-15",
                  "email": "sefa@example.com",
                  "phoneNumber": "05551234567",
                  "hireDate": "2020-01-10",
                  "employmentStatus": "ACTIVE"
                }
                """;
    }
}
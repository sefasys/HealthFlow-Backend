package com.healthflow.presentation.controller.clinicregistrar;

import com.healthflow.application.dto.clinicregistrar.*;
import com.healthflow.application.exception.ClinicRegistrarAlreadyExistsException;
import com.healthflow.application.exception.ClinicRegistrarNotFoundException;
import com.healthflow.application.exception.InvalidSearchQueryException;
import com.healthflow.application.exception.InvalidUpdateRequestException;
import com.healthflow.application.service.ClinicRegistrarService;
import com.healthflow.domain.model.user.staff.EmploymentStatus;
import com.healthflow.presentation.controller.ClinicRegistrarController;
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

@WebMvcTest(ClinicRegistrarController.class)
@Import(GlobalExceptionHandler.class)
class ClinicRegistrarControllerTest {

    private static final String BASE_URL = "/api/clinicregistrar";

    private static final UUID REGISTRAR_ID =
            UUID.fromString("22222222-2222-4222-8222-222222222222");

    private static final String NATIONAL_ID = "10000000146";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClinicRegistrarService clinicRegistrarService;

    // --------------------------------------------------
    // CREATE
    // --------------------------------------------------

    @Test
    void createClinicRegistrar_shouldReturn201_whenRequestIsValid()
            throws Exception {

        CreateClinicRegistrarRequestDto expectedRequest =
                new CreateClinicRegistrarRequestDto(
                        NATIONAL_ID,
                        "Sefa",
                        "Soysal",
                        LocalDate.of(1990, 5, 15),
                        "sefa@example.com",
                        "05551234567",
                        LocalDate.of(2020, 1, 10),
                        EmploymentStatus.ACTIVE
                );

        when(clinicRegistrarService.createClinicRegistrar(expectedRequest))
                .thenReturn(registrarResponse());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(REGISTRAR_ID.toString()))
                .andExpect(jsonPath("$.name").value("Sefa"))
                .andExpect(jsonPath("$.surname").value("Soysal"))
                .andExpect(jsonPath("$.birthDate").value("1990-05-15"))
                .andExpect(jsonPath("$.hireDate").value("2020-01-10"))
                .andExpect(jsonPath("$.employmentStatus").value("ACTIVE"));

        verify(clinicRegistrarService)
                .createClinicRegistrar(expectedRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "nationalId", "name", "surname", "email", "phoneNumber"
    })
    void createClinicRegistrar_shouldReturn400_whenRequiredTextIsBlank(
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

        verifyNoInteractions(clinicRegistrarService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "birthDate", "hireDate", "employmentStatus"
    })
    void createClinicRegistrar_shouldReturn400_whenRequiredFieldIsNull(
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

        verifyNoInteractions(clinicRegistrarService);
    }

    @Test
    void createClinicRegistrar_shouldReturn400_whenEmailIsInvalid()
            throws Exception {

        String json = validCreateJson()
                .replace("sefa@example.com", "invalid-email");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(clinicRegistrarService);
    }

    @Test
    void createClinicRegistrar_shouldReturn400_whenBirthDateIsInFuture()
            throws Exception {

        String json = validCreateJson().replace(
                "1990-05-15",
                LocalDate.now().plusYears(1).toString()
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(clinicRegistrarService);
    }

    @Test
    void createClinicRegistrar_shouldReturn400_whenEmploymentStatusIsUnknown()
            throws Exception {

        String json = validCreateJson()
                .replace("\"ACTIVE\"", "\"UNKNOWN_STATUS\"");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicRegistrarService);
    }

    @Test
    void createClinicRegistrar_shouldReturn409_whenRegistrarAlreadyExists()
            throws Exception {

        when(clinicRegistrarService.createClinicRegistrar(
                any(CreateClinicRegistrarRequestDto.class)
        )).thenThrow(new ClinicRegistrarAlreadyExistsException(
                "There is already a clinic registrar with the same national ID."
        ));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code")
                        .value("RESOURCE_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value(
                        "There is already a clinic registrar with the same national ID."
                ))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createClinicRegistrar_shouldReturn400_whenJsonIsMalformed()
            throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicRegistrarService);
    }

    // --------------------------------------------------
    // GET ALL
    // --------------------------------------------------

    @Test
    void getClinicRegistrars_shouldReturn200AndList()
            throws Exception {

        when(clinicRegistrarService.getClinicRegistrars())
                .thenReturn(List.of(registrarResponse()));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(REGISTRAR_ID.toString()))
                .andExpect(jsonPath("$[0].name").value("Sefa"));

        verify(clinicRegistrarService).getClinicRegistrars();
    }

    @Test
    void getClinicRegistrars_shouldReturn200AndEmptyArray_whenNoneExist()
            throws Exception {

        when(clinicRegistrarService.getClinicRegistrars())
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(clinicRegistrarService).getClinicRegistrars();
    }

    @Test
    void getClinicRegistrars_shouldReturn500_whenUnexpectedErrorOccurs()
            throws Exception {

        when(clinicRegistrarService.getClinicRegistrars())
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
    void findByUniqueId_shouldReturn200_whenRegistrarExists()
            throws Exception {

        when(clinicRegistrarService.findClinicRegistrarByUniqueId(REGISTRAR_ID))
                .thenReturn(registrarResponse());

        mockMvc.perform(get(
                        BASE_URL + "/find-by-unique-id/{uniqueId}",
                        REGISTRAR_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REGISTRAR_ID.toString()))
                .andExpect(jsonPath("$.name").value("Sefa"));

        verify(clinicRegistrarService)
                .findClinicRegistrarByUniqueId(REGISTRAR_ID);
    }

    @Test
    void findByUniqueId_shouldReturn404_whenRegistrarDoesNotExist()
            throws Exception {

        when(clinicRegistrarService.findClinicRegistrarByUniqueId(REGISTRAR_ID))
                .thenThrow(new ClinicRegistrarNotFoundException(
                        "Clinic registrar not found."
                ));

        mockMvc.perform(get(
                        BASE_URL + "/find-by-unique-id/{uniqueId}",
                        REGISTRAR_ID
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Clinic registrar not found."));
    }

    @Test
    void findByUniqueId_shouldReturn400_whenUuidIsInvalid()
            throws Exception {

        mockMvc.perform(get(
                        BASE_URL + "/find-by-unique-id/{uniqueId}",
                        "invalid-uuid"
                ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicRegistrarService);
    }

    // --------------------------------------------------
    // FIND BY NATIONAL ID
    // --------------------------------------------------

    @Test
    void findByNationalId_shouldReturn200_whenRegistrarExists()
            throws Exception {

        FindClinicRegistrarByNationalIdRequestDto expectedRequest =
                new FindClinicRegistrarByNationalIdRequestDto(NATIONAL_ID);

        when(clinicRegistrarService.findClinicRegistrarByNationalId(
                expectedRequest
        )).thenReturn(registrarResponse());

        mockMvc.perform(post(BASE_URL + "/find-by-national-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nationalId": "10000000146"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REGISTRAR_ID.toString()));

        verify(clinicRegistrarService)
                .findClinicRegistrarByNationalId(expectedRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"nationalId\": \" \"}",
            "{\"nationalId\": null}",
            "{}"
    })
    void findByNationalId_shouldReturn400_whenNationalIdIsMissingOrBlank(
            String json
    ) throws Exception {

        mockMvc.perform(post(BASE_URL + "/find-by-national-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicRegistrarService);
    }

    @Test
    void findByNationalId_shouldReturn404_whenRegistrarDoesNotExist()
            throws Exception {

        FindClinicRegistrarByNationalIdRequestDto expectedRequest =
                new FindClinicRegistrarByNationalIdRequestDto(NATIONAL_ID);

        when(clinicRegistrarService.findClinicRegistrarByNationalId(
                expectedRequest
        )).thenThrow(new ClinicRegistrarNotFoundException(
                "Clinic registrar not found with this national id."
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
    void updateClinicRegistrar_shouldReturn200_whenRequestIsValid()
            throws Exception {

        UpdateClinicRegistrarRequestDto expectedRequest =
                new UpdateClinicRegistrarRequestDto(
                        "new@example.com",
                        "05559876543"
                );

        when(clinicRegistrarService.updateClinicRegistrar(
                REGISTRAR_ID, expectedRequest
        )).thenReturn(updateResponse("new@example.com", "05559876543"));

        mockMvc.perform(patch(
                        BASE_URL + "/update/{uniqueId}",
                        REGISTRAR_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "new@example.com",
                                  "phoneNumber": "05559876543"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REGISTRAR_ID.toString()))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("05559876543"));

        verify(clinicRegistrarService)
                .updateClinicRegistrar(REGISTRAR_ID, expectedRequest);
    }

    @Test
    void updateClinicRegistrar_shouldReturn200_whenOnlyEmailIsProvided()
            throws Exception {

        UpdateClinicRegistrarRequestDto expectedRequest =
                new UpdateClinicRegistrarRequestDto("new@example.com", null);

        when(clinicRegistrarService.updateClinicRegistrar(
                REGISTRAR_ID, expectedRequest
        )).thenReturn(updateResponse("new@example.com", "05551234567"));

        mockMvc.perform(patch(
                        BASE_URL + "/update/{uniqueId}",
                        REGISTRAR_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "new@example.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("05551234567"));

        verify(clinicRegistrarService)
                .updateClinicRegistrar(REGISTRAR_ID, expectedRequest);
    }

    @Test
    void updateClinicRegistrar_shouldReturn200_whenOnlyPhoneIsProvided()
            throws Exception {

        UpdateClinicRegistrarRequestDto expectedRequest =
                new UpdateClinicRegistrarRequestDto(null, "05559876543");

        when(clinicRegistrarService.updateClinicRegistrar(
                REGISTRAR_ID, expectedRequest
        )).thenReturn(updateResponse("sefa@example.com", "05559876543"));

        mockMvc.perform(patch(
                        BASE_URL + "/update/{uniqueId}",
                        REGISTRAR_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"phoneNumber": "05559876543"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("sefa@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("05559876543"));

        verify(clinicRegistrarService)
                .updateClinicRegistrar(REGISTRAR_ID, expectedRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"email\": \"invalid-email\"}",
            "{\"email\": \" \"}",
            "{\"phoneNumber\": \" \"}"
    })
    void updateClinicRegistrar_shouldReturn400_whenContactDataIsInvalid(
            String json
    ) throws Exception {

        mockMvc.perform(patch(
                        BASE_URL + "/update/{uniqueId}",
                        REGISTRAR_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicRegistrarService);
    }

    @Test
    void updateClinicRegistrar_shouldReturn400_whenNoFieldsAreProvided()
            throws Exception {

        UpdateClinicRegistrarRequestDto expectedRequest =
                new UpdateClinicRegistrarRequestDto(null, null);

        when(clinicRegistrarService.updateClinicRegistrar(
                REGISTRAR_ID, expectedRequest
        )).thenThrow(new InvalidUpdateRequestException(
                "At least one field must be provided for update."
        ));

        mockMvc.perform(patch(
                        BASE_URL + "/update/{uniqueId}",
                        REGISTRAR_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(
                        "At least one field must be provided for update."
                ));

        verify(clinicRegistrarService)
                .updateClinicRegistrar(REGISTRAR_ID, expectedRequest);
    }

    @Test
    void updateClinicRegistrar_shouldReturn404_whenRegistrarDoesNotExist()
            throws Exception {

        UpdateClinicRegistrarRequestDto expectedRequest =
                new UpdateClinicRegistrarRequestDto("new@example.com", null);

        when(clinicRegistrarService.updateClinicRegistrar(
                REGISTRAR_ID, expectedRequest
        )).thenThrow(new ClinicRegistrarNotFoundException(
                "Clinic registrar could not be found."
        ));

        mockMvc.perform(patch(
                        BASE_URL + "/update/{uniqueId}",
                        REGISTRAR_ID
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
    void searchClinicRegistrar_shouldReturn200AndMatchingRegistrars()
            throws Exception {

        when(clinicRegistrarService.searchClinicRegistrar("Sefa"))
                .thenReturn(List.of(registrarResponse()));

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", "Sefa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(REGISTRAR_ID.toString()))
                .andExpect(jsonPath("$[0].name").value("Sefa"));

        verify(clinicRegistrarService).searchClinicRegistrar("Sefa");
    }

    @Test
    void searchClinicRegistrar_shouldReturn200AndEmptyArray_whenNoMatchExists()
            throws Exception {

        when(clinicRegistrarService.searchClinicRegistrar("NoMatch"))
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", "NoMatch"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(clinicRegistrarService).searchClinicRegistrar("NoMatch");
    }

    @Test
    void searchClinicRegistrar_shouldReturn400_whenQueryParameterIsMissing()
            throws Exception {

        mockMvc.perform(get(BASE_URL + "/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(clinicRegistrarService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void searchClinicRegistrar_shouldReturn400_whenQueryIsBlank(
            String query
    ) throws Exception {

        when(clinicRegistrarService.searchClinicRegistrar(query))
                .thenThrow(new InvalidSearchQueryException(
                        "Search query cannot be null or blank."
                ));

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", query))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(
                        "Search query cannot be null or blank."
                ));

        verify(clinicRegistrarService).searchClinicRegistrar(query);
    }

    // --------------------------------------------------
    // TEST DATA
    // --------------------------------------------------

    private ClinicRegistrarResponseDto registrarResponse() {
        return new ClinicRegistrarResponseDto(
                REGISTRAR_ID,
                "Sefa",
                "Soysal",
                LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10),
                EmploymentStatus.ACTIVE
        );
    }

    private UpdateClinicRegistrarResponseDto updateResponse(
            String email,
            String phoneNumber
    ) {
        return new UpdateClinicRegistrarResponseDto(
                REGISTRAR_ID,
                "Sefa",
                "Soysal",
                LocalDate.of(1990, 5, 15),
                email,
                phoneNumber
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
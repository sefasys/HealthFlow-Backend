package com.healthflow.presentation.controller.availability;

import com.healthflow.application.dto.availability.*;
import com.healthflow.application.exception.AvailabilityConflictException;
import com.healthflow.application.exception.AvailabilityNotFoundException;
import com.healthflow.application.exception.ClinicRegistrarNotFoundException;
import com.healthflow.application.exception.ClinicianNotFoundException;
import com.healthflow.application.service.AvailabilityService;
import com.healthflow.domain.exception.InvalidAvailabilityException;
import com.healthflow.domain.model.appointment.AvailabilityStatus;
import com.healthflow.presentation.controller.AvailabilityController;
import com.healthflow.presentation.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvailabilityController.class)
@Import(GlobalExceptionHandler.class)
class AvailabilityControllerTest {

    private static final String BASE_URL = "/api/availabilities";

    private static final UUID AVAILABILITY_ID =
            UUID.fromString("11111111-1111-4111-8111-111111111111");

    private static final UUID CLINICIAN_ID =
            UUID.fromString("22222222-2222-4222-8222-222222222222");

    private static final UUID REGISTRAR_ID =
            UUID.fromString("33333333-3333-4333-8333-333333333333");

    // Servis mock olduğu için gerçek zaman kuralları burada çalışmaz.
    private static final LocalDate DATE = LocalDate.of(2030, 6, 15);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvailabilityService availabilityService;

    // CREATE

    @Test
    void createAvailability_shouldReturn201_whenRequestIsValid()
            throws Exception {

        when(availabilityService.createAvailability(
                CLINICIAN_ID, createRequest()
        )).thenReturn(response(AvailabilityStatus.PENDING_REVIEW));

        mockMvc.perform(post(BASE_URL)
                        .param("clinicianId", CLINICIAN_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uniqueId")
                        .value(AVAILABILITY_ID.toString()))
                .andExpect(jsonPath("$.clinicianId")
                        .value(CLINICIAN_ID.toString()))
                .andExpect(jsonPath("$.date").value(DATE.toString()))
                .andExpect(jsonPath("$.startTime")
                        .value(anyOf(is("09:00"), is("09:00:00"))))
                .andExpect(jsonPath("$.endTime")
                        .value(anyOf(is("10:00"), is("10:00:00"))))
                .andExpect(jsonPath("$.status").value("PENDING_REVIEW"));

        verify(availabilityService)
                .createAvailability(CLINICIAN_ID, createRequest());
    }

    @Test
    void createAvailability_shouldReturn400_whenClinicianIdIsMissing()
            throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(availabilityService);
    }

    @Test
    void createAvailability_shouldReturn400_whenClinicianIdIsInvalid()
            throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .param("clinicianId", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(availabilityService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            """
            {"startTime":"09:00","endTime":"10:00"}
            """,
            """
            {"date":"2030-06-15","endTime":"10:00"}
            """,
            """
            {"date":"2030-06-15","startTime":"09:00"}
            """,
            """
            {"date":"invalid-date","startTime":"09:00","endTime":"10:00"}
            """,
            """
            {"date":"2030-06-15","startTime":"25:00","endTime":"10:00"}
            """,
            "{\"date\":"
    })
    void createAvailability_shouldReturn400_whenBodyIsInvalid(
            String json
    ) throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .param("clinicianId", CLINICIAN_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(availabilityService);
    }

    @ParameterizedTest
    @MethodSource("creationFailures")
    void createAvailability_shouldMapServiceException(
            RuntimeException exception,
            int expectedStatus,
            String expectedCode
    ) throws Exception {

        when(availabilityService.createAvailability(
                CLINICIAN_ID, createRequest()
        )).thenThrow(exception);

        mockMvc.perform(post(BASE_URL)
                        .param("clinicianId", CLINICIAN_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.code").value(expectedCode))
                .andExpect(jsonPath("$.message")
                        .value(exception.getMessage()));

        verify(availabilityService)
                .createAvailability(CLINICIAN_ID, createRequest());
    }

    private static Stream<Arguments> creationFailures() {
        return Stream.of(
                Arguments.of(
                        new ClinicianNotFoundException(
                                "Clinician could not be found."
                        ),
                        404,
                        "RESOURCE_NOT_FOUND"
                ),
                Arguments.of(
                        new AvailabilityConflictException(
                                "There is a conflict in time ranges."
                        ),
                        409,
                        "SCHEDULING_CONFLICT"
                ),
                Arguments.of(
                        new InvalidAvailabilityException(
                                "Availability start must be in the future."
                        ),
                        400,
                        "DOMAIN_VALIDATION_ERROR"
                )
        );
    }

    // LIST / FIND

    @Test
    void getAvailabilities_shouldReturn200AndList()
            throws Exception {

        when(availabilityService.getAvailabilities())
                .thenReturn(List.of(
                        response(AvailabilityStatus.PENDING_REVIEW)
                ));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].uniqueId")
                        .value(AVAILABILITY_ID.toString()));

        verify(availabilityService).getAvailabilities();
    }

    @Test
    void getAvailabilities_shouldReturn200AndEmptyArray_whenNoneExist()
            throws Exception {

        when(availabilityService.getAvailabilities()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(availabilityService).getAvailabilities();
    }

    @Test
    void getAvailabilityById_shouldReturn200_whenFound()
            throws Exception {

        when(availabilityService.getAvailabilityById(AVAILABILITY_ID))
                .thenReturn(response(AvailabilityStatus.PUBLISHED));

        mockMvc.perform(get(BASE_URL + "/{id}", AVAILABILITY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueId")
                        .value(AVAILABILITY_ID.toString()))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        verify(availabilityService).getAvailabilityById(AVAILABILITY_ID);
    }

    @Test
    void getAvailabilityById_shouldReturn404_whenNotFound()
            throws Exception {

        when(availabilityService.getAvailabilityById(AVAILABILITY_ID))
                .thenThrow(new AvailabilityNotFoundException(
                        "Availability not found."
                ));

        mockMvc.perform(get(BASE_URL + "/{id}", AVAILABILITY_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void getByClinician_shouldForwardClinicianId()
            throws Exception {

        when(availabilityService.getAvailabilitiesByClinicianId(CLINICIAN_ID))
                .thenReturn(List.of(
                        response(AvailabilityStatus.PUBLISHED)
                ));

        mockMvc.perform(get(
                        BASE_URL + "/clinician/{id}", CLINICIAN_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clinicianId")
                        .value(CLINICIAN_ID.toString()));

        verify(availabilityService)
                .getAvailabilitiesByClinicianId(CLINICIAN_ID);
    }

    @Test
    void getByClinicianAndDate_shouldForwardParsedDate()
            throws Exception {

        when(availabilityService.getAvailabilitiesByClinicianAndDate(
                CLINICIAN_ID, DATE
        )).thenReturn(List.of(response(AvailabilityStatus.PUBLISHED)));

        mockMvc.perform(get(
                        BASE_URL + "/clinician/{id}/date/{date}",
                        CLINICIAN_ID,
                        DATE
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value(DATE.toString()));

        verify(availabilityService)
                .getAvailabilitiesByClinicianAndDate(CLINICIAN_ID, DATE);
    }

    @Test
    void getByClinicianAndDate_shouldReturn400_whenDateIsInvalid()
            throws Exception {

        mockMvc.perform(get(
                        BASE_URL + "/clinician/{id}/date/{date}",
                        CLINICIAN_ID,
                        "not-a-date"
                ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(availabilityService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/invalid-uuid",
            "/clinician/invalid-uuid",
            "/clinician/invalid-uuid/date/2030-06-15"
    })
    void getAvailability_shouldReturn400_whenPathUuidIsInvalid(
            String path
    ) throws Exception {

        mockMvc.perform(get(BASE_URL + path))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(availabilityService);
    }

    @Test
    void getPendingAvailabilities_shouldUsePendingEndpoint()
            throws Exception {

        when(availabilityService.getPendingAvailabilities())
                .thenReturn(List.of(
                        response(AvailabilityStatus.PENDING_REVIEW)
                ));

        mockMvc.perform(get(BASE_URL + "/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING_REVIEW"));

        verify(availabilityService).getPendingAvailabilities();
        verifyNoMoreInteractions(availabilityService);
    }

    // PUBLISH / REJECT

    @Test
    void publishAvailability_shouldReturn200AndPublishedResponse()
            throws Exception {

        when(availabilityService.publishAvailability(
                AVAILABILITY_ID, REGISTRAR_ID
        )).thenReturn(response(AvailabilityStatus.PUBLISHED));

        mockMvc.perform(post(
                        BASE_URL + "/{id}/publish", AVAILABILITY_ID
                )
                        .param("registrarUserId", REGISTRAR_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueId")
                        .value(AVAILABILITY_ID.toString()))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        verify(availabilityService)
                .publishAvailability(AVAILABILITY_ID, REGISTRAR_ID);
    }

    @Test
    void publishAvailability_shouldReturn404_whenRegistrarIsNotFound()
            throws Exception {

        when(availabilityService.publishAvailability(
                AVAILABILITY_ID, REGISTRAR_ID
        )).thenThrow(new ClinicRegistrarNotFoundException(
                "Clinic registrar not found."
        ));

        mockMvc.perform(post(
                        BASE_URL + "/{id}/publish", AVAILABILITY_ID
                )
                        .param("registrarUserId", REGISTRAR_ID.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void publishAvailability_shouldReturn400_whenDomainRejectsOperation()
            throws Exception {

        when(availabilityService.publishAvailability(
                AVAILABILITY_ID, REGISTRAR_ID
        )).thenThrow(new InvalidAvailabilityException(
                "Availability start must be in the future."
        ));

        mockMvc.perform(post(
                        BASE_URL + "/{id}/publish", AVAILABILITY_ID
                )
                        .param("registrarUserId", REGISTRAR_ID.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value("DOMAIN_VALIDATION_ERROR"));
    }

    @Test
    void rejectAvailability_shouldReturn200AndRejectionReason()
            throws Exception {

        RejectAvailabilityRequestDto request =
                new RejectAvailabilityRequestDto("Schedule needs revision.");

        when(availabilityService.rejectAvailability(
                AVAILABILITY_ID, REGISTRAR_ID, request
        )).thenReturn(response(AvailabilityStatus.REJECTED));

        mockMvc.perform(post(
                        BASE_URL + "/{id}/reject", AVAILABILITY_ID
                )
                        .param("registrarUserId", REGISTRAR_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reason":"Schedule needs revision."}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.rejectionReason")
                        .value("Schedule needs revision."));

        verify(availabilityService)
                .rejectAvailability(AVAILABILITY_ID, REGISTRAR_ID, request);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"reason\":null}",
            "{\"reason\":\"\"}",
            "{\"reason\":\" \"}"
    })
    void rejectAvailability_shouldReturn400_whenReasonIsMissingOrBlank(
            String json
    ) throws Exception {

        mockMvc.perform(post(
                        BASE_URL + "/{id}/reject", AVAILABILITY_ID
                )
                        .param("registrarUserId", REGISTRAR_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(availabilityService);
    }

    @Test
    void rejectAvailability_shouldReturn404_whenAvailabilityIsNotFound()
            throws Exception {

        RejectAvailabilityRequestDto request =
                new RejectAvailabilityRequestDto("Schedule needs revision.");

        when(availabilityService.rejectAvailability(
                AVAILABILITY_ID, REGISTRAR_ID, request
        )).thenThrow(new AvailabilityNotFoundException(
                "Availability not found."
        ));

        mockMvc.perform(post(
                        BASE_URL + "/{id}/reject", AVAILABILITY_ID
                )
                        .param("registrarUserId", REGISTRAR_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reason":"Schedule needs revision."}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"publish", "reject"})
    void reviewAvailability_shouldReturn400_whenRegistrarIdIsMissing(
            String operation
    ) throws Exception {

        mockMvc.perform(post(
                        BASE_URL + "/{id}/{operation}",
                        AVAILABILITY_ID, operation
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reason":"Schedule needs revision."}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(availabilityService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"publish", "reject"})
    void reviewAvailability_shouldReturn400_whenRegistrarIdIsInvalid(
            String operation
    ) throws Exception {

        mockMvc.perform(post(
                        BASE_URL + "/{id}/{operation}",
                        AVAILABILITY_ID, operation
                )
                        .param("registrarUserId", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reason":"Schedule needs revision."}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(availabilityService);
    }

    @Test
    void getAvailabilities_shouldReturn500_whenUnexpectedErrorOccurs()
            throws Exception {

        when(availabilityService.getAvailabilities())
                .thenThrow(new RuntimeException("Internal database detail"));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("An unexpected error occurred."));
    }

    // TEST DATA

    private CreateAvailabilityRequestDto createRequest() {
        return new CreateAvailabilityRequestDto(
                DATE,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0)
        );
    }

    private AvailabilityResponseDto response(AvailabilityStatus status) {
        return new AvailabilityResponseDto(
                AVAILABILITY_ID,
                CLINICIAN_ID,
                DATE,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                status,
                status == AvailabilityStatus.REJECTED
                        ? "Schedule needs revision."
                        : null
        );
    }

    private String validCreateJson() {
        return """
                {
                  "date":"2030-06-15",
                  "startTime":"09:00",
                  "endTime":"10:00"
                }
                """;
    }
}
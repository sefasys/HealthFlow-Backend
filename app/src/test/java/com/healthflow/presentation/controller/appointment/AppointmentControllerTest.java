package com.healthflow.presentation.controller.appointment;

import com.healthflow.application.dto.appointment.*;
import com.healthflow.application.exception.AppointmentAccessDeniedException;
import com.healthflow.application.exception.AppointmentNotFoundException;
import com.healthflow.application.exception.AvailabilityNotFoundException;
import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.application.service.AppointmentService;
import com.healthflow.domain.exception.AppointmentConflictException;
import com.healthflow.domain.exception.InvalidAppointmentException;
import com.healthflow.domain.model.appointment.AppointmentStatus;
import com.healthflow.presentation.controller.AppointmentController;
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

@WebMvcTest(AppointmentController.class)
@Import(GlobalExceptionHandler.class)
class AppointmentControllerTest {

    private static final String BASE_URL = "/api/appointments";

    private static final UUID APPOINTMENT_ID =
            UUID.fromString("11111111-1111-4111-8111-111111111111");

    private static final UUID AVAILABILITY_ID =
            UUID.fromString("22222222-2222-4222-8222-222222222222");

    private static final UUID PATIENT_ID =
            UUID.fromString("33333333-3333-4333-8333-333333333333");

    private static final UUID CLINICIAN_ID =
            UUID.fromString("44444444-4444-4444-8444-444444444444");

    private static final LocalDate DATE = LocalDate.of(2030, 6, 15);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    // BOOK

    @Test
    void bookAppointment_shouldReturn201_whenRequestIsValid()
            throws Exception {

        when(appointmentService.bookAppointment(
                PATIENT_ID, bookingRequest()
        )).thenReturn(response(AppointmentStatus.SCHEDULED));

        mockMvc.perform(post(BASE_URL)
                        .param("patientUserId", PATIENT_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookingJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uniqueId")
                        .value(APPOINTMENT_ID.toString()))
                .andExpect(jsonPath("$.availabilityId")
                        .value(AVAILABILITY_ID.toString()))
                .andExpect(jsonPath("$.patientId").value(PATIENT_ID.toString()))
                .andExpect(jsonPath("$.clinicianId")
                        .value(CLINICIAN_ID.toString()))
                .andExpect(jsonPath("$.date").value(DATE.toString()))
                .andExpect(jsonPath("$.startTime")
                        .value(anyOf(is("09:00"), is("09:00:00"))))
                .andExpect(jsonPath("$.endTime")
                        .value(anyOf(is("09:15"), is("09:15:00"))))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));

        verify(appointmentService)
                .bookAppointment(PATIENT_ID, bookingRequest());
    }

    @Test
    void bookAppointment_shouldReturn400_whenPatientIdIsMissing()
            throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookingJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(appointmentService);
    }

    @Test
    void bookAppointment_shouldReturn400_whenPatientIdIsInvalid()
            throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .param("patientUserId", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookingJson()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(appointmentService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"startTime\":\"09:00\"}",
            """
            {"availabilityId":"22222222-2222-4222-8222-222222222222"}
            """,
            """
            {"availabilityId":null,"startTime":"09:00"}
            """,
            """
            {"availabilityId":"invalid-uuid","startTime":"09:00"}
            """,
            """
            {
              "availabilityId":"22222222-2222-4222-8222-222222222222",
              "startTime":"25:00"
            }
            """,
            "{\"availabilityId\":"
    })
    void bookAppointment_shouldReturn400_whenBodyIsInvalid(
            String json
    ) throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .param("patientUserId", PATIENT_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(appointmentService);
    }

    @ParameterizedTest
    @MethodSource("bookingFailures")
    void bookAppointment_shouldMapServiceException(
            RuntimeException exception,
            int expectedStatus,
            String expectedCode
    ) throws Exception {

        when(appointmentService.bookAppointment(
                PATIENT_ID, bookingRequest()
        )).thenThrow(exception);

        mockMvc.perform(post(BASE_URL)
                        .param("patientUserId", PATIENT_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookingJson()))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.code").value(expectedCode))
                .andExpect(jsonPath("$.message")
                        .value(exception.getMessage()));

        verify(appointmentService)
                .bookAppointment(PATIENT_ID, bookingRequest());
    }

    private static Stream<Arguments> bookingFailures() {
        return Stream.of(
                Arguments.of(
                        new PatientNotFoundException(
                                "Patient could not be found."
                        ),
                        404,
                        "RESOURCE_NOT_FOUND"
                ),
                Arguments.of(
                        new AvailabilityNotFoundException(
                                "Availability could not be found."
                        ),
                        404,
                        "RESOURCE_NOT_FOUND"
                ),
                Arguments.of(
                        new AppointmentConflictException(
                                "The selected slot is already booked."
                        ),
                        409,
                        "SCHEDULING_CONFLICT"
                ),
                Arguments.of(
                        new InvalidAppointmentException(
                                "The appointment cannot be booked."
                        ),
                        400,
                        "DOMAIN_VALIDATION_ERROR"
                )
        );
    }

    // AVAILABLE SLOTS

    @Test
    void getAvailableSlots_shouldReturn200AndSlots()
            throws Exception {

        AppointmentSlotResponseDto slot = new AppointmentSlotResponseDto(
                AVAILABILITY_ID,
                DATE,
                LocalTime.of(9, 0),
                LocalTime.of(9, 15)
        );

        when(appointmentService.getAvailableSlots(AVAILABILITY_ID))
                .thenReturn(List.of(slot));

        mockMvc.perform(get(BASE_URL + "/slots")
                        .param("availabilityId", AVAILABILITY_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].availabilityId")
                        .value(AVAILABILITY_ID.toString()))
                .andExpect(jsonPath("$[0].date").value(DATE.toString()))
                .andExpect(jsonPath("$[0].startTime")
                        .value(anyOf(is("09:00"), is("09:00:00"))))
                .andExpect(jsonPath("$[0].endTime")
                        .value(anyOf(is("09:15"), is("09:15:00"))));

        verify(appointmentService).getAvailableSlots(AVAILABILITY_ID);
    }

    @Test
    void getAvailableSlots_shouldReturn200AndEmptyArray_whenNoSlotsExist()
            throws Exception {

        when(appointmentService.getAvailableSlots(AVAILABILITY_ID))
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/slots")
                        .param("availabilityId", AVAILABILITY_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(appointmentService).getAvailableSlots(AVAILABILITY_ID);
    }

    @Test
    void getAvailableSlots_shouldReturn400_whenAvailabilityIdIsMissing()
            throws Exception {

        mockMvc.perform(get(BASE_URL + "/slots"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(appointmentService);
    }

    @Test
    void getAvailableSlots_shouldReturn400_whenAvailabilityIdIsInvalid()
            throws Exception {

        mockMvc.perform(get(BASE_URL + "/slots")
                        .param("availabilityId", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(appointmentService);
    }

    @Test
    void getAvailableSlots_shouldReturn404_whenAvailabilityIsNotFound()
            throws Exception {

        when(appointmentService.getAvailableSlots(AVAILABILITY_ID))
                .thenThrow(new AvailabilityNotFoundException(
                        "Availability could not be found."
                ));

        mockMvc.perform(get(BASE_URL + "/slots")
                        .param("availabilityId", AVAILABILITY_ID.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    // FIND / LIST

    @Test
    void getAppointmentById_shouldReturn200_whenFound()
            throws Exception {

        when(appointmentService.getAppointmentById(APPOINTMENT_ID))
                .thenReturn(response(AppointmentStatus.SCHEDULED));

        mockMvc.perform(get(BASE_URL + "/{id}", APPOINTMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueId")
                        .value(APPOINTMENT_ID.toString()))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));

        verify(appointmentService).getAppointmentById(APPOINTMENT_ID);
    }

    @Test
    void getAppointmentById_shouldReturn404_whenNotFound()
            throws Exception {

        when(appointmentService.getAppointmentById(APPOINTMENT_ID))
                .thenThrow(new AppointmentNotFoundException(
                        "Appointment not found."
                ));

        mockMvc.perform(get(BASE_URL + "/{id}", APPOINTMENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void getAppointmentsByPatient_shouldForwardPatientId()
            throws Exception {

        when(appointmentService.getAppointmentsByPatientId(PATIENT_ID))
                .thenReturn(List.of(response(AppointmentStatus.SCHEDULED)));

        mockMvc.perform(get(BASE_URL + "/patient/{id}", PATIENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].patientId")
                        .value(PATIENT_ID.toString()));

        verify(appointmentService).getAppointmentsByPatientId(PATIENT_ID);
    }

    @Test
    void getAppointmentsByPatient_shouldReturnEmptyArray_whenNoneExist()
            throws Exception {

        when(appointmentService.getAppointmentsByPatientId(PATIENT_ID))
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/patient/{id}", PATIENT_ID))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(appointmentService).getAppointmentsByPatientId(PATIENT_ID);
    }

    @Test
    void getAppointmentsByClinicianAndDate_shouldForwardParsedDate()
            throws Exception {

        when(appointmentService.getAppointmentsByClinicianAndDate(
                CLINICIAN_ID, DATE
        )).thenReturn(List.of(response(AppointmentStatus.SCHEDULED)));

        mockMvc.perform(get(
                        BASE_URL + "/clinician/{id}/date/{date}",
                        CLINICIAN_ID, DATE
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clinicianId")
                        .value(CLINICIAN_ID.toString()))
                .andExpect(jsonPath("$[0].date").value(DATE.toString()));

        verify(appointmentService)
                .getAppointmentsByClinicianAndDate(CLINICIAN_ID, DATE);
    }

    @Test
    void getAppointmentsByClinicianAndDate_shouldReturn400_whenDateIsInvalid()
            throws Exception {

        mockMvc.perform(get(
                        BASE_URL + "/clinician/{id}/date/{date}",
                        CLINICIAN_ID, "not-a-date"
                ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(appointmentService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/invalid-uuid",
            "/patient/invalid-uuid",
            "/clinician/invalid-uuid/date/2030-06-15"
    })
    void getAppointments_shouldReturn400_whenPathUuidIsInvalid(
            String path
    ) throws Exception {

        mockMvc.perform(get(BASE_URL + path))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(appointmentService);
    }

    // CANCEL

    @Test
    void cancelAppointment_shouldReturn200AndCancelledResponse()
            throws Exception {

        when(appointmentService.cancelAppointment(
                APPOINTMENT_ID, PATIENT_ID
        )).thenReturn(response(AppointmentStatus.CANCELLED));

        mockMvc.perform(post(
                        BASE_URL + "/{id}/cancel", APPOINTMENT_ID
                )
                        .param("patientUserId", PATIENT_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueId")
                        .value(APPOINTMENT_ID.toString()))
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(appointmentService)
                .cancelAppointment(APPOINTMENT_ID, PATIENT_ID);
    }

    @Test
    void cancelAppointment_shouldReturn403_whenAccessIsDenied()
            throws Exception {

        when(appointmentService.cancelAppointment(
                APPOINTMENT_ID, PATIENT_ID
        )).thenThrow(new AppointmentAccessDeniedException(
                "You can only cancel your own appointments."
        ));

        mockMvc.perform(post(
                        BASE_URL + "/{id}/cancel", APPOINTMENT_ID
                )
                        .param("patientUserId", PATIENT_ID.toString()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value(
                        "You can only cancel your own appointments."
                ));

        verify(appointmentService)
                .cancelAppointment(APPOINTMENT_ID, PATIENT_ID);
    }

    @Test
    void cancelAppointment_shouldReturn404_whenAppointmentIsNotFound()
            throws Exception {

        when(appointmentService.cancelAppointment(
                APPOINTMENT_ID, PATIENT_ID
        )).thenThrow(new AppointmentNotFoundException(
                "Appointment not found."
        ));

        mockMvc.perform(post(
                        BASE_URL + "/{id}/cancel", APPOINTMENT_ID
                )
                        .param("patientUserId", PATIENT_ID.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void cancelAppointment_shouldReturn400_whenPatientIdIsMissing()
            throws Exception {

        mockMvc.perform(post(
                        BASE_URL + "/{id}/cancel", APPOINTMENT_ID
                ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(appointmentService);
    }

    @Test
    void cancelAppointment_shouldReturn400_whenPatientIdIsInvalid()
            throws Exception {

        mockMvc.perform(post(
                        BASE_URL + "/{id}/cancel", APPOINTMENT_ID
                )
                        .param("patientUserId", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(appointmentService);
    }

    @Test
    void cancelAppointment_shouldReturn400_whenAppointmentIdIsInvalid()
            throws Exception {

        mockMvc.perform(post(
                        BASE_URL + "/{id}/cancel", "invalid-uuid"
                )
                        .param("patientUserId", PATIENT_ID.toString()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(appointmentService);
    }

    @Test
    void getAppointmentById_shouldReturn500_whenUnexpectedErrorOccurs()
            throws Exception {

        when(appointmentService.getAppointmentById(APPOINTMENT_ID))
                .thenThrow(new RuntimeException("Internal database detail"));

        mockMvc.perform(get(BASE_URL + "/{id}", APPOINTMENT_ID))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("An unexpected error occurred."));
    }

    // TEST DATA

    private BookAppointmentRequestDto bookingRequest() {
        return new BookAppointmentRequestDto(
                AVAILABILITY_ID,
                LocalTime.of(9, 0)
        );
    }

    private AppointmentResponseDto response(AppointmentStatus status) {
        return new AppointmentResponseDto(
                APPOINTMENT_ID,
                AVAILABILITY_ID,
                PATIENT_ID,
                CLINICIAN_ID,
                DATE,
                LocalTime.of(9, 0),
                LocalTime.of(9, 15),
                status
        );
    }

    private String validBookingJson() {
        return """
                {
                  "availabilityId":"22222222-2222-4222-8222-222222222222",
                  "startTime":"09:00"
                }
                """;
    }
}
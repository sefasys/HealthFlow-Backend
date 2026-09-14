package com.healthflow.presentation.controller;

import com.healthflow.application.exception.PatientAlreadyExistsException;
import com.healthflow.application.usecase.patient.CreatePatientUseCase;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.patient.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class) // MOCK KULLANIYORUZ BURADA. DİKKAT ET BURADA MOCK'A.
class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreatePatientUseCase createPatientUseCase;

    @Test
    void shouldReturn400WhenRequestIsInvalid() throws Exception {

        mockMvc.perform(
                        post("/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "nationalId": "",
                              "name": "",
                              "surname": "",
                              "birthDate": null,
                              "email": "invalid-email",
                              "phoneNumber": ""
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldReturn409WhenPatientAlreadyExists() throws Exception {

        doThrow(new PatientAlreadyExistsException("Patient already exists"))
                .when(createPatientUseCase)
                .execute(
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any()
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
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code").value("PATIENT_ALREADY_EXISTS"));
    }

    @Test
    void shouldReturn500WhenUnexpectedExceptionOccurs() throws Exception {

        doThrow(new RuntimeException("Unexpected error"))
                .when(createPatientUseCase)
                .execute(
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any()
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
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
    }

    @Test
    void shouldReturn201WhenPatientCreatedSuccessfully() throws Exception {

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

        when(createPatientUseCase.execute(
                any(NationalId.class),
                any(String.class),
                any(String.class),
                any(LocalDate.class),
                any(String.class),
                any(String.class)
        )).thenReturn(patient);
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
                .andExpect(status().isCreated());
    }

}
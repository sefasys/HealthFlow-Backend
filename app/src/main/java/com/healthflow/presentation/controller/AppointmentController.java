package com.healthflow.presentation.controller;

import com.healthflow.application.dto.appointment.AppointmentResponseDto;
import com.healthflow.application.dto.appointment.AppointmentSlotResponseDto;
import com.healthflow.application.dto.appointment.BookAppointmentRequestDto;
import com.healthflow.application.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponseDto bookAppointment(
            @RequestParam("patientUserId") UUID patientUserId,
            @RequestBody BookAppointmentRequestDto request
    ) {
        return appointmentService.bookAppointment(patientUserId, request);
    }

    @GetMapping("/slots")
    @ResponseStatus(HttpStatus.OK)
    public List<AppointmentSlotResponseDto> getAvailableSlots(
            @RequestParam("availabilityId") UUID availabilityId
    ) {
        return appointmentService.getAvailableSlots(availabilityId);
    }

    @GetMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.OK)
    public AppointmentResponseDto getAppointmentById(
            @PathVariable("appointmentId") UUID appointmentId
    ) {
        return appointmentService.getAppointmentById(appointmentId);
    }

    @GetMapping("/patient/{patientUserId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AppointmentResponseDto> getAppointmentsByPatientId(
            @PathVariable("patientUserId") UUID patientUserId
    ) {
        return appointmentService.getAppointmentsByPatientId(patientUserId);
    }

    @GetMapping("/clinician/{clinicianUserId}/date/{date}")
    @ResponseStatus(HttpStatus.OK)
    public List<AppointmentResponseDto> getAppointmentsByClinicianAndDate(
            @PathVariable("clinicianUserId") UUID clinicianUserId,
            @PathVariable("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return appointmentService.getAppointmentsByClinicianAndDate(
                clinicianUserId, date
        );
    }

    @PostMapping("/{appointmentId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public AppointmentResponseDto cancelAppointment(
            @PathVariable("appointmentId") UUID appointmentId,
            @RequestParam("patientUserId") UUID patientUserId
    ) {
        return appointmentService.cancelAppointment(appointmentId, patientUserId);
    }
}
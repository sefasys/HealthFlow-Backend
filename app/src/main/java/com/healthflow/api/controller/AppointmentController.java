package com.healthflow.api.controller;

import com.healthflow.dto.appointment.AppointmentResponse;
import com.healthflow.dto.appointment.BookAppointmentRequest;
import com.healthflow.dto.appointment.OpenSlotsRequest;
import com.healthflow.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/availabilities/{availabilityId}/appointments")
    public ResponseEntity<List<AppointmentResponse>> openSlots(
            @PathVariable UUID availabilityId,
            @Valid @RequestBody OpenSlotsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.openSlots(availabilityId, request));
    }

    @GetMapping("/availabilities/{availabilityId}/appointments")
    public List<AppointmentResponse> getSlots(@PathVariable UUID availabilityId) {
        return appointmentService.getByAvailability(availabilityId);
    }

    @PostMapping("/appointments/{id}/book")
    public AppointmentResponse book(@PathVariable UUID id,
                                    @Valid @RequestBody BookAppointmentRequest request) {
        return appointmentService.book(id, request);
    }

    @PostMapping("/appointments/{id}/release")
    public AppointmentResponse release(@PathVariable UUID id) {
        return appointmentService.release(id);
    }


}
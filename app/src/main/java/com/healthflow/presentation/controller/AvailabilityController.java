package com.healthflow.presentation.controller;

import com.healthflow.application.dto.availability.AvailabilityResponseDto;
import com.healthflow.application.dto.availability.CreateAvailabilityRequestDto;
import com.healthflow.application.dto.availability.RejectAvailabilityRequestDto;
import com.healthflow.application.service.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/availabilities")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvailabilityResponseDto createAvailability(
            @RequestParam("clinicianId") UUID clinicianId,
            @RequestBody CreateAvailabilityRequestDto request
    ) {
        return availabilityService.createAvailability(clinicianId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilityResponseDto> getAvailabilities() {
        return availabilityService.getAvailabilities();
    }

    @GetMapping("/{availabilityId}")
    @ResponseStatus(HttpStatus.OK)
    public AvailabilityResponseDto getAvailabilityById(
            @PathVariable("availabilityId") UUID availabilityId
    ) {
        return availabilityService.getAvailabilityById(availabilityId);
    }

    @GetMapping("/clinician/{clinicianId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilityResponseDto> getAvailabilitiesByClinicianId(
            @PathVariable("clinicianId") UUID clinicianId
    ) {
        return availabilityService.getAvailabilitiesByClinicianId(clinicianId);
    }

    @GetMapping("/clinician/{clinicianId}/date/{date}")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilityResponseDto> getAvailabilitiesByClinicianAndDate(
            @PathVariable("clinicianId") UUID clinicianId,
            @PathVariable("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return availabilityService.getAvailabilitiesByClinicianAndDate(clinicianId, date);
    }

    @GetMapping("/pending")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilityResponseDto> getPendingAvailabilities() {
        return availabilityService.getPendingAvailabilities();
    }

    @PostMapping("/{availabilityId}/publish")
    @ResponseStatus(HttpStatus.OK)
    public AvailabilityResponseDto publishAvailability(
            @PathVariable("availabilityId") UUID availabilityId,
            @RequestParam("registrarUserId") UUID registrarUserId
    ) {
        return availabilityService.publishAvailability(availabilityId, registrarUserId);
    }

    @PostMapping("/{availabilityId}/reject")
    @ResponseStatus(HttpStatus.OK)
    public AvailabilityResponseDto rejectAvailability(
            @PathVariable("availabilityId") UUID availabilityId,
            @RequestParam("registrarUserId") UUID registrarUserId,
            @RequestBody RejectAvailabilityRequestDto request
    ) {
        return availabilityService.rejectAvailability(availabilityId, registrarUserId, request);
    }
}
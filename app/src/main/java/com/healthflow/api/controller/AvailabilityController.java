package com.healthflow.api.controller;

import com.healthflow.dto.availability.AvailabilityResponse;
import com.healthflow.dto.availability.CreateAvailabilityRequest;
import com.healthflow.service.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinicians/{clinicianId}/availabilities")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }
    @PostMapping
    public ResponseEntity<AvailabilityResponse> create(
            @PathVariable UUID clinicianId,
            @Valid @RequestBody CreateAvailabilityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(availabilityService.create(clinicianId, request));
    }

    @GetMapping
    public List<AvailabilityResponse> getActive(@PathVariable UUID clinicianId) {
        return availabilityService.getActiveByClinician(clinicianId);
    }

}
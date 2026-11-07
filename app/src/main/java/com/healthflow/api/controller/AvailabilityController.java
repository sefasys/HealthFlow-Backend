package com.healthflow.api.controller;

import com.healthflow.dto.availability.AvailabilityResponseDto;
import com.healthflow.dto.availability.CreateAvailabilityRequestDto;
import com.healthflow.dto.availability.RejectAvailabilityRequestDto;
import com.healthflow.service.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/availabilities")
public class AvailabilityController {


}
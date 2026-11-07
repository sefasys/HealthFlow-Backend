package com.healthflow.api.controller;

import com.healthflow.dto.appointment.AppointmentResponseDto;
import com.healthflow.dto.appointment.AppointmentSlotResponseDto;
import com.healthflow.dto.appointment.BookAppointmentRequestDto;
import com.healthflow.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {




}
package com.healthflow.service;


import com.healthflow.dto.appointment.AppointmentResponse;
import com.healthflow.dto.appointment.BookAppointmentRequest;
import com.healthflow.dto.appointment.OpenSlotsRequest;
import com.healthflow.entity.appointment.Appointment;
import com.healthflow.entity.appointment.AppointmentStatus;
import com.healthflow.entity.availability.Availability;
import com.healthflow.entity.patient.Patient;
import com.healthflow.entity.staff.ClinicRegistrar;
import com.healthflow.exception.*;
import com.healthflow.mapper.appointment.AppointmentMapper;
import com.healthflow.repository.appointment.IAppointmentRepository;
import com.healthflow.repository.availability.IAvailabilityRepository;
import com.healthflow.repository.clinicregistrar.IClinicRegistrarRepository;
import com.healthflow.repository.patient.IPatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final IAppointmentRepository appointmentRepository;
    private final IAvailabilityRepository availabilityRepository;
    private final IClinicRegistrarRepository registrarRepository;
    private final IPatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentService(IAppointmentRepository appointmentRepository, IAvailabilityRepository availabilityRepository, IClinicRegistrarRepository registrarRepository, IPatientRepository patientRepository, AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.availabilityRepository = availabilityRepository;
        this.registrarRepository = registrarRepository;
        this.patientRepository = patientRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Transactional
    public AppointmentResponse book(UUID appointmentId, BookAppointmentRequest request) {
        Appointment appointment = findAppointment(appointmentId);

        if (!appointment.getStartTime().isAfter(LocalDateTime.now())) {
            throw new InvalidAppointmentException("Cannot book a slot in the past");
        }

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new PatientNotFoundException(
                        "Patient not found with ID: " + request.patientId()));

        if (appointmentRepository.existsPatientOverlap(patient.getId(),
                appointment.getStartTime(), appointment.getEndTime(), AppointmentStatus.BOOKED)) {
            throw new AppointmentConflictException("Patient already has an appointment at this time");
        }

        appointment.book(patient);
        return appointmentMapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse release(UUID appointmentId) {
        Appointment appointment = findAppointment(appointmentId);
        appointment.release();
        return appointmentMapper.toResponse(appointment);
    }

    private Appointment findAppointment(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + id));
    }


    @Transactional
    public List<AppointmentResponse> openSlots(UUID availabilityId, OpenSlotsRequest request) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new AvailabilityNotFoundException("Availability not found with ID: " + availabilityId));

        if (!availability.isActive()) {
            throw new InvalidAvailabilityException("Availability is not active");
        }

        ClinicRegistrar registrar = registrarRepository.findDetailedById(request.registrarId())
                .orElseThrow(() -> new ClinicRegistrarNotFoundException("Clinic registrar not found with ID: " + request.registrarId()));

        if (!registrar.getStaff().isActive()) {
            throw new InvalidAppointmentException("Only active registrars can open slots");
        }

        if (appointmentRepository.existsByAvailabilityId(availabilityId)) {
            throw new AppointmentConflictException("Slots for this availability are already opened");
        }

        List<Appointment> slots = createSlots(availability, registrar);
        appointmentRepository.saveAll(slots);

        return slots.stream().map(appointmentMapper::toResponse).toList();
    }

    private List<Appointment> createSlots(Availability availability, ClinicRegistrar registrar) {
        List<Appointment> slots = new ArrayList<>();
        int minutes = availability.getSlotDuration().getMinutes();
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime slotStart = availability.getStartTime();
        while (slotStart.isBefore(availability.getEndTime())) {
            LocalDateTime slotEnd = slotStart.plusMinutes(minutes);
            if (slotStart.isAfter(now)) {
                slots.add(new Appointment(availability, registrar, slotStart, slotEnd));
            }
            slotStart = slotEnd;
        }
        return slots;
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getByAvailability(UUID availabilityId) {
        if (!availabilityRepository.existsById(availabilityId)) {
            throw new AvailabilityNotFoundException("Availability not found with id: " + availabilityId);
        }
        return appointmentRepository.findByAvailabilityIdOrderByStartTime(availabilityId)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }
}

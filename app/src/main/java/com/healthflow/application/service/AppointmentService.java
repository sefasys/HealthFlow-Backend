package com.healthflow.application.service;

import com.healthflow.application.dto.appointment.AppointmentResponseDto;
import com.healthflow.application.dto.appointment.AppointmentSlotResponseDto;
import com.healthflow.application.dto.appointment.BookAppointmentRequestDto;
import com.healthflow.application.exception.AppointmentAccessDeniedException;
import com.healthflow.application.exception.AppointmentNotFoundException;
import com.healthflow.application.exception.AvailabilityNotFoundException;
import com.healthflow.application.exception.PatientNotFoundException;
import com.healthflow.application.mapper.appointment.AppointmentMapper;
import com.healthflow.domain.exception.InvalidDateException;
import com.healthflow.domain.exception.InvalidUniqueIdException;
import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.appointment.AppointmentSlot;
import com.healthflow.domain.model.appointment.Availability;
import com.healthflow.domain.model.appointment.TimeRange;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.model.user.staff.EmploymentStatus;
import com.healthflow.domain.service.AppointmentScheduler;
import com.healthflow.domain.service.SlotGenerator;
import com.healthflow.port.repository.IAppointmentRepository;
import com.healthflow.port.repository.IAvailabilityRepository;
import com.healthflow.port.repository.IPatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AppointmentService {

    private final IAppointmentRepository appointmentRepository;
    private final IAvailabilityRepository availabilityRepository;
    private final IPatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentScheduler appointmentScheduler;
    private final SlotGenerator slotGenerator;

    public AppointmentService(
            IAppointmentRepository appointmentRepository,
            IAvailabilityRepository availabilityRepository,
            IPatientRepository patientRepository,
            AppointmentMapper appointmentMapper,
            AppointmentScheduler appointmentScheduler,
            SlotGenerator slotGenerator
    ) {
        this.appointmentRepository = appointmentRepository;
        this.availabilityRepository = availabilityRepository;
        this.patientRepository = patientRepository;
        this.appointmentMapper = appointmentMapper;
        this.appointmentScheduler = appointmentScheduler;
        this.slotGenerator = slotGenerator;
    }

    public AppointmentResponseDto bookAppointment(
            UUID patientUserId,
            BookAppointmentRequestDto request
    ) {
        // 1. Randevuyu alacak hastayı bul.
        Patient patient = patientRepository.findByUniqueId(patientUserId)
                .orElseThrow(() ->
                        new PatientNotFoundException("Patient could not be found.")
                );

        // 2. Hastanın seçtiği müsaitlik kaydını bul.
        Availability availability = availabilityRepository
                .findById(request.availabilityId())
                .orElseThrow(() ->
                        new AvailabilityNotFoundException(
                                "Availability could not be found."
                        )
                );

        // 3. Seçilen başlangıç saatinden 15 dakikalık aralık oluştur.
        TimeRange timeRange = new TimeRange(
                request.startTime(),
                request.startTime().plus(Availability.SLOT_DURATION)
        );

        UUID clinicianUserId = availability.getClinician()
                .getStaff()
                .getUser()
                .getUniqueId();

        // 4. Her iki kişinin o günkü bütün rollerindeki randevularını getir.
        List<Appointment> patientAppointments =
                appointmentRepository.findByUserIdAndDate(
                        patientUserId,
                        availability.getDate()
                );

        List<Appointment> clinicianAppointments =
                appointmentRepository.findByUserIdAndDate(
                        clinicianUserId,
                        availability.getDate()
                );

        // 5. İki listede de bulunabilen randevuları ID üzerinden tekilleştir.
        Map<UUID, Appointment> existingAppointments = new HashMap<>();

        patientAppointments.forEach(appointment ->
                existingAppointments.put(appointment.getUniqueId(), appointment)
        );

        clinicianAppointments.forEach(appointment ->
                existingAppointments.put(appointment.getUniqueId(), appointment)
        );

        // 6. Domain kurallarını uygulayarak randevuyu oluştur.
        Appointment appointment = appointmentScheduler.bookAppointment(
                UUID.randomUUID(),
                patient,
                availability,
                timeRange,
                List.copyOf(existingAppointments.values())
        );

        // 7. Kaydet ve response DTO'ya dönüştür.
        Appointment savedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.responseDto(savedAppointment);
    }

    public AppointmentResponseDto getAppointmentById(UUID appointmentId) {
        if (appointmentId == null) {
            throw new InvalidUniqueIdException(
                    "Appointment ID cannot be null."
            );
        }

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() ->
                        new AppointmentNotFoundException(
                                "Appointment not found with ID: " + appointmentId
                        )
                );

        return appointmentMapper.responseDto(appointment);
    }

    public List<AppointmentResponseDto> getAppointmentsByPatientId(
            UUID patientUserId
    ) {
        if (patientUserId == null) {
            throw new InvalidUniqueIdException(
                    "Patient user ID cannot be null."
            );
        }

        List<Appointment> appointments =
                appointmentRepository.findByPatientId(patientUserId);

        return appointmentMapper.responseDtoList(appointments);
    }

    public List<AppointmentResponseDto> getAppointmentsByClinicianAndDate(
            UUID clinicianUserId,
            LocalDate date
    ) {
        if (clinicianUserId == null) {
            throw new InvalidUniqueIdException(
                    "Clinician user ID cannot be null."
            );
        }

        if (date == null) {
            throw new InvalidDateException(
                    "Appointment date cannot be null."
            );
        }

        List<Appointment> appointments =
                appointmentRepository.findByClinicianIdAndDate(
                        clinicianUserId,
                        date
                );

        return appointmentMapper.responseDtoList(appointments);
    }

    public AppointmentResponseDto cancelAppointment(
            UUID appointmentId,
            UUID patientUserId
    ) {
        if (appointmentId == null || patientUserId == null) {
            throw new InvalidUniqueIdException(
                    "Appointment ID and patient user ID cannot be null."
            );
        }

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() ->
                        new AppointmentNotFoundException(
                                "Appointment not found with ID: " + appointmentId
                        )
                );

        // Hasta yalnızca kendi randevusunu iptal edebilir.
        UUID appointmentPatientId = appointment.getPatient()
                .getUser()
                .getUniqueId();

        if (!appointmentPatientId.equals(patientUserId)) {
            throw new AppointmentAccessDeniedException(
                    "You can only cancel your own appointments."
            );
        }

        appointment.cancel();

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return appointmentMapper.responseDto(savedAppointment);
    }

    public List<AppointmentSlotResponseDto> getAvailableSlots(
            UUID availabilityId
    ) {
        if (availabilityId == null) {
            throw new InvalidUniqueIdException(
                    "Availability ID cannot be null."
            );
        }

        Availability availability = availabilityRepository
                .findById(availabilityId)
                .orElseThrow(() ->
                        new AvailabilityNotFoundException(
                                "Availability could not be found."
                        )
                );

        // Yayımlanmamış veya doktoru aktif olmayan kayıtları sunma.
        if (!availability.isPublished()
                || availability.getClinician().getStaff().getEmploymentStatus()
                != EmploymentStatus.ACTIVE) {
            return List.of();
        }

        UUID clinicianUserId = availability.getClinician()
                .getStaff()
                .getUser()
                .getUniqueId();

        // Doktorun hasta rolündeki randevularını da hesaba kat.
        List<Appointment> appointments =
                appointmentRepository.findByUserIdAndDate(
                        clinicianUserId,
                        availability.getDate()
                );

        List<AppointmentSlot> slots =
                slotGenerator.generateSlots(availability, appointments);

        LocalDateTime now = LocalDateTime.now(
                ZoneId.of("Europe/Istanbul")
        );

        List<AppointmentSlot> availableSlots = slots.stream()
                .filter(AppointmentSlot::isAvailable)
                .filter(slot ->
                        LocalDateTime.of(
                                slot.date(),
                                slot.timeRange().start()
                        ).isAfter(now)
                )
                .toList();

        return appointmentMapper.slotResponseDtoList(availableSlots);
    }
}

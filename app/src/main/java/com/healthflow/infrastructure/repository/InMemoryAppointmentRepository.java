package com.healthflow.infrastructure.repository;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.port.repository.IAppointmentRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class InMemoryAppointmentRepository implements IAppointmentRepository {

    private final Map<UUID, Appointment> appointmentMap = new HashMap<>();

    @Override
    public Appointment save(Appointment appointment) {
        appointmentMap.put(appointment.getUniqueId(), appointment);
        return appointment;
    }

    @Override
    public Optional<Appointment> findById(UUID appointmentId) {
        return Optional.ofNullable(appointmentMap.get(appointmentId));
    }

    @Override
    public List<Appointment> findByPatientId(UUID patientUserId) {
        return appointmentMap.values().stream()
                .filter(appointment ->
                        appointment.getPatient()
                                .getUser()
                                .getUniqueId()
                                .equals(patientUserId)
                )
                .toList();
    }

    @Override
    public List<Appointment> findByClinicianIdAndDate(
            UUID clinicianUserId,
            LocalDate date
    ) {
        return appointmentMap.values().stream()
                .filter(appointment ->
                        appointment.getClinician()
                                .getStaff()
                                .getUser()
                                .getUniqueId()
                                .equals(clinicianUserId)
                )
                .filter(appointment -> appointment.getDate().equals(date))
                .toList();
    }

    @Override
    public List<Appointment> findByUserIdAndDate(
            UUID userId,
            LocalDate date
    ) {
        return appointmentMap.values().stream()
                .filter(appointment -> appointment.getDate().equals(date))
                .filter(appointment -> appointment.involvesUser(userId))
                .toList();
    }
}

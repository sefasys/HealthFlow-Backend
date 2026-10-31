package com.healthflow.infrastructure.persistence.adapter;

import com.healthflow.application.mapper.patient.PatientMapper;
import com.healthflow.domain.factory.StaffFactory;
import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.infrastructure.persistence.entity.PatientEntity;
import com.healthflow.infrastructure.persistence.entity.StaffEntity;
import com.healthflow.infrastructure.persistence.mapper.AppointmentPersistenceMapper;
import com.healthflow.infrastructure.persistence.repository.*;
import com.healthflow.port.repository.IAppointmentRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAppointmentRepositoryAdapter implements IAppointmentRepository {

    private final SpringDataPatientRepository patientRepository;
    private final SpringDataStaffRepository staffRepository;
    private final SpringDataAppointmentRepository appointmentRepository;
    private final AppointmentPersistenceMapper mapper;
    private final PatientMapper patientMapper;
    private final StaffFactory staffFactory;

    public JpaAppointmentRepositoryAdapter(SpringDataPatientRepository patientRepository, SpringDataStaffRepository staffRepository, SpringDataAppointmentRepository appointmentRepository, AppointmentPersistenceMapper mapper, PatientMapper patientMapper, StaffFactory staffFactory) {
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.appointmentRepository = appointmentRepository;
        this.mapper = mapper;
        this.patientMapper = patientMapper;
        this.staffFactory = staffFactory;
    }

    @Override
    @Transactional
    public Appointment save(Appointment appointment) {
        appointmentRepository.findById(appointment.getUniqueId()).ifPresentOrElse(
                entity -> entity.changeStatus(appointment.getStatus()),
                () -> {
                    PatientEntity patient = patientRepository.getReferenceById(
                            appointment.getPatient().getUniqueId());
                    StaffEntity clinician = staffRepository.getReferenceById(
                            appointment.getClinician().getStaff().getUserId());
                    appointmentRepository.save(mapper.toEntity(appointment, patient, clinician));
                });
        return appointment;
    }

    @Override
    public Optional<Appointment> findById(UUID appointmentId) {
        return appointmentRepository.findWithParticipantsById(appointmentId).map(mapper::toDomain);
    }

    @Override
    public List<Appointment> findByPatientId(UUID patientUserId) {
        return appointmentRepository.findByPatientId(patientUserId).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<Appointment> findByClinicianIdAndDate(UUID clinicianUserId, LocalDate date) {
        return appointmentRepository.findByClinicianIdAndDate(clinicianUserId, date).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<Appointment> findByUserIdAndDate(UUID userId, LocalDate date) {
        return appointmentRepository.findByUserIdAndDate(userId, date).stream()
                .map(mapper::toDomain).toList();
    }
}

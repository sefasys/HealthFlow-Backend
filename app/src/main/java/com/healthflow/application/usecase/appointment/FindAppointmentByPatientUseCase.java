package com.healthflow.application.usecase.appointment;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.AppointmentRepository;

import java.util.List;

public class FindAppointmentByPatientUseCase {
    private final AppointmentRepository appointmentRepository;

    public FindAppointmentByPatientUseCase(AppointmentRepository appointmentRepository){
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> execute(Patient patient){
        return appointmentRepository.findByPatient(patient);
    }
}

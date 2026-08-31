package com.healthflow.application.usecase.appointment;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.port.repository.AppointmentRepository;

public class CreateAppointmentUseCase {
    private final AppointmentRepository appointmentRepository;

    public CreateAppointmentUseCase(AppointmentRepository appointmentRepository){
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment execute(Appointment appointment){
        if (appointmentRepository
                .findByUniqueId(appointment.getUniqueId())
                .isPresent()) {
            throw new IllegalArgumentException(
                    "Appointment already exists"
            );
        }

        appointmentRepository.addAppointment(appointment);

        return appointment;
    }

}

package com.healthflow.infrastructure.repository;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.port.repository.AppointmentRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class InMemoryAppointmentRepository implements AppointmentRepository {
        List<Appointment> appointments;

        public InMemoryAppointmentRepository(List<Appointment> appointments){
            if(appointments == null)
                throw new IllegalArgumentException(
                        "Appointments can not be null"
                );

            this.appointments = appointments;
        }


    @Override
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    @Override
    public Optional<Appointment> findByUniqueId(Long uniqueId) {
        return appointments.stream().filter(appointment -> appointment.getUniqueId().equals(uniqueId)).findFirst();
    }

    @Override
    public List<Appointment> getAppointments() {
        return appointments.stream().toList();
    }

    @Override
    public List<Appointment> findByPatient(Patient patient) {
        return appointments.stream().filter(appointment -> appointment.getPatient().equals(patient)).toList();
    }

    @Override
    public List<Appointment> findByClinician(Clinician clinician) {
        return appointments.stream().filter(appointment -> appointment.getClinician().equals(clinician)).toList();
    }
}

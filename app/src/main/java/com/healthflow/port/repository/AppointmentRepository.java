package com.healthflow.port.repository;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.model.user.staff.Clinician;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository {
  void addAppointment(Appointment appointment);

  Optional<Appointment> findByUniqueId(UUID uniqueId);

  List<Appointment> getAppointments();

  List<Appointment> findByPatient(Patient patient);

  List<Appointment> findByClinician(Clinician clinician);
}

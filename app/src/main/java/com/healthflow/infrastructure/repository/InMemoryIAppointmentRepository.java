package com.healthflow.infrastructure.repository;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.port.repository.IAppointmentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryIAppointmentRepository implements IAppointmentRepository {
  List<Appointment> appointments;

  public InMemoryIAppointmentRepository(List<Appointment> appointments) {
    if (appointments == null) throw new IllegalArgumentException("Appointments can not be null");

    this.appointments =
        new ArrayList<>(
            appointments); // Bu tarz işlemler de this.appointments = appointments; kullanılmaz bu
    // tehlikelidir ve dışarıdan birisi falan değiştirebilir.
    // Onun yerine this.appointments = new ArrayList<>(appointments); şeklinde yazılabilir.
  }

  @Override
  public void addAppointment(Appointment appointment) {
    appointments.add(appointment);
  }

  @Override
  public Optional<Appointment> findByUniqueId(UUID uniqueId) {
    return appointments.stream()
        .filter(appointment -> appointment.getUniqueId().equals(uniqueId))
        .findFirst();
  }

  @Override
  public List<Appointment> getAppointments() {
    return appointments.stream().toList();
  }

  @Override
  public List<Appointment> findByPatient(Patient patient) {
    return appointments.stream()
        .filter(appointment -> appointment.getPatient().equals(patient))
        .toList();
  }

  @Override
  public List<Appointment> findByClinician(Clinician clinician) {
    return appointments.stream()
        .filter(appointment -> appointment.getClinician().equals(clinician))
        .toList();
  }
}

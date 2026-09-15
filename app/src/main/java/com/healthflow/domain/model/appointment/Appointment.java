package com.healthflow.domain.model.appointment;

import com.healthflow.domain.exception.InvalidAppointmentException;
import com.healthflow.domain.model.clinicaldepartment.ClinicalDepartment;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.model.user.staff.Clinician;
import java.time.LocalDate;
import java.util.UUID;

public class Appointment {

  private final UUID uniqueId;
  private AppointmentStatus status;
  private final Patient patient;
  private final Clinician clinician;
  private final TimeRange timeRange;
  private final ClinicalDepartment clinicalDepartment;
  private final LocalDate date;

  public Appointment(
      UUID uniqueId,
      Patient patient,
      Clinician clinician,
      LocalDate date,
      TimeRange timeRange,
      ClinicalDepartment clinicalDepartment) {

    if (uniqueId == null) {
      throw new InvalidAppointmentException("Unique ID cannot be null");
    }
    if (patient == null) {
      throw new InvalidAppointmentException("Patient cannot be null");
    }

    if (clinician == null) {
      throw new InvalidAppointmentException("Clinician cannot be null");
    }

    if (date == null) {
      throw new InvalidAppointmentException("Date cannot be null");
    }

    if (timeRange == null) {
      throw new InvalidAppointmentException("Time range cannot be null");
    }
    if (clinicalDepartment == null) {
      throw new InvalidAppointmentException("Clinical department cannot be null");
    }
    this.uniqueId = uniqueId;
    this.patient = patient;
    this.clinician = clinician;
    this.date = date;
    this.timeRange = timeRange;
    this.status = AppointmentStatus.SCHEDULED;
    this.clinicalDepartment = clinicalDepartment;
  }

  public ClinicalDepartment getClinicalDepartment() {
    return clinicalDepartment;
  }

  public Patient getPatient() {
    return patient;
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public Clinician getClinician() {
    return clinician;
  }

  public LocalDate getDate() {
    return date;
  }

  public TimeRange getTimeRange() {
    return timeRange;
  }

  public AppointmentStatus getStatus() {
    return status;
  }

  public void cancel() {
    if (status == AppointmentStatus.COMPLETED) {
      throw new InvalidAppointmentException("Completed appointment cannot be cancelled");
    }

    status = AppointmentStatus.CANCELLED;
  }

  public void complete() {
    if (status != AppointmentStatus.SCHEDULED) {
      throw new InvalidAppointmentException("Only scheduled appointments can be completed");
    }

    status = AppointmentStatus.COMPLETED;
  }

  public void markAsNoShow() {
    if (status != AppointmentStatus.SCHEDULED) {
      throw new InvalidAppointmentException("Only scheduled appointments can be marked as no-show");
    }

    status = AppointmentStatus.NO_SHOW;
  }
}

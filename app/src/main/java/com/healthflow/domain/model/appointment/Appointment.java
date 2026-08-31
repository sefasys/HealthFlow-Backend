package com.healthflow.domain.model.appointment;

import com.healthflow.domain.model.clinicaldepartment.ClinicalDepartment;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.model.user.staff.Clinician;
import java.time.LocalDate;

public class Appointment {

  private final Long uniqueId;
  private AppointmentStatus status;
  private final Patient patient;
  private final Clinician clinician;
  private final TimeRange timeRange;
  private final ClinicalDepartment clinicalDepartment;
  private final LocalDate date;

  public Appointment(
      Long uniqueId,
      Patient patient,
      Clinician clinician,
      LocalDate date,
      TimeRange timeRange,
      ClinicalDepartment clinicalDepartment) {

    if (uniqueId == null) {
      throw new IllegalArgumentException("Unique ID cannot be null");
    }
    if (patient == null) {
      throw new IllegalArgumentException("Patient cannot be null");
    }

    if (clinician == null) {
      throw new IllegalArgumentException("Clinician cannot be null");
    }

    if (date == null) {
      throw new IllegalArgumentException("Date cannot be null");
    }

    if (timeRange == null) {
      throw new IllegalArgumentException("Time range cannot be null");
    }
    if (clinicalDepartment == null) {
      throw new IllegalArgumentException("Clinical department cannot be null");
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

  public Long getUniqueId() {
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
      throw new IllegalStateException("Completed appointment cannot be cancelled");
    }

    status = AppointmentStatus.CANCELLED;
  }

  public void complete() {
    if (status != AppointmentStatus.SCHEDULED) {
      throw new IllegalStateException("Only scheduled appointments can be completed");
    }

    status = AppointmentStatus.COMPLETED;
  }

  public void markAsNoShow() {
    if (status != AppointmentStatus.SCHEDULED) {
      throw new IllegalStateException("Only scheduled appointments can be marked as no-show");
    }

    status = AppointmentStatus.NO_SHOW;
  }
}

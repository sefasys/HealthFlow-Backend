package com.healthflow.entity.appointment;


import com.healthflow.entity.user.patient.Patient;
import com.healthflow.entity.user.staff.Clinician;
import java.time.LocalDate;
import java.util.UUID;

public class Appointment {
  private final UUID uniqueId;
  private final UUID availabilityId;
  private final Patient patient;
  private final Clinician clinician;
  private final LocalDate date;
  private final TimeRange timeRange;
  private AppointmentStatus status;

}

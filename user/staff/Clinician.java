package user.staff;

import appointment.Appointment;
import appointment.Availability;
import clinicaldepartment.ClinicalDepartment;
import user.patient.Patient;

import java.util.List;

public class Clinician{
    Staff staff;
    ClinicalDepartment department;
    List<Availability> availabilities;
    List<Patient> patients;
    List<Appointment> appointments;
}

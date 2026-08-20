package appointment;

import clinicaldepartment.ClinicalDepartment;
import user.patient.Patient;
import user.staff.Clinician;

import java.time.LocalDateTime;

public class Appointment {

    private AppointmentStatus appointmentStatus;
    private Patient patient;
    private Clinician clinician;
    private LocalDateTime appointmentDateTime;
    private ClinicalDepartment clinicalDepartment;
    private Availability availability;

}

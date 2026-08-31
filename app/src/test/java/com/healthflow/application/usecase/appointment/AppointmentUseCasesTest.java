package com.healthflow.application.usecase.appointment;

import com.healthflow.domain.model.appointment.Appointment;
import com.healthflow.domain.model.appointment.TimeRange;
import com.healthflow.domain.model.clinicaldepartment.Cardiology;
import com.healthflow.domain.model.clinicaldepartment.ClinicalDepartment;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.patient.BloodType;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.domain.model.user.staff.Clinician;
import com.healthflow.domain.model.user.staff.EmploymentStatus;
import com.healthflow.domain.model.user.staff.Staff;
import com.healthflow.infrastructure.repository.InMemoryAppointmentRepository;
import com.healthflow.port.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentUseCasesTest {

    private AppointmentRepository repository;
    private CreateAppointmentUseCase createAppointment;
    private GetAppointmentsUseCase getAppointments;
    private FindAppointmentByUniqueIdUseCase findByUniqueId;
    private FindAppointmentByPatientUseCase findByPatient;
    private FindAppointmentByClinicianUseCase findByClinician;

    private Patient patient1;
    private Patient patient2;
    private Clinician clinician1;
    private Clinician clinician2;
    private ClinicalDepartment cardiology;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAppointmentRepository(new ArrayList<>());
        createAppointment = new CreateAppointmentUseCase(repository);
        getAppointments = new GetAppointmentsUseCase(repository);
        findByUniqueId = new FindAppointmentByUniqueIdUseCase(repository);
        findByPatient = new FindAppointmentByPatientUseCase(repository);
        findByClinician = new FindAppointmentByClinicianUseCase(repository);

        patient1 = patient(1L, "12345678902", "Sefa", "Soysal");
        patient2 = patient(2L, "12345678904", "Ali", "Kaya");
        cardiology = new Cardiology();
        clinician1 = clinician(101L, "12345678906", "Ayse", "Doktor", "EMP-1", cardiology);
        clinician2 = clinician(102L, "12345678908", "Mehmet", "Doktor", "EMP-2", cardiology);
    }

    @Test
    void createAppointment_shouldAddAppointmentToRepository() {
        Appointment appointment = appointment(1L, patient1, clinician1, LocalDate.of(2026, 9, 1), 10, 0, 10, 15);

        Appointment result = createAppointment.execute(appointment);

        assertSame(appointment, result);
        assertEquals(1, getAppointments.execute().size());
        assertSame(appointment, getAppointments.execute().getFirst());
    }

    @Test
    void createAppointment_shouldRejectDuplicateUniqueId() {
        Appointment first = appointment(50L, patient1, clinician1, LocalDate.of(2026, 9, 1), 10, 0, 10, 15);
        Appointment second = appointment(50L, patient2, clinician2, LocalDate.of(2026, 9, 2), 11, 0, 11, 15);

        createAppointment.execute(first);

        assertThrows(IllegalArgumentException.class, () -> createAppointment.execute(second));
        assertEquals(1, getAppointments.execute().size());
    }

    @Test
    void findByUniqueId_shouldReturnMatchingAppointment() {
        Appointment appointment = appointment(15L, patient1, clinician1, LocalDate.of(2026, 9, 1), 10, 0, 10, 15);
        createAppointment.execute(appointment);

        var result = findByUniqueId.execute(15L);

        assertTrue(result.isPresent());
        assertSame(appointment, result.get());
    }

    @Test
    void findByUniqueId_shouldReturnEmptyWhenAppointmentDoesNotExist() {
        assertTrue(findByUniqueId.execute(999L).isEmpty());
    }

    @Test
    void findByPatient_shouldReturnOnlyThatPatientsAppointments() {
        Appointment first = appointment(1L, patient1, clinician1, LocalDate.of(2026, 9, 1), 10, 0, 10, 15);
        Appointment second = appointment(2L, patient2, clinician1, LocalDate.of(2026, 9, 1), 10, 15, 10, 30);
        Appointment third = appointment(3L, patient1, clinician2, LocalDate.of(2026, 9, 2), 11, 0, 11, 15);
        createAppointment.execute(first);
        createAppointment.execute(second);
        createAppointment.execute(third);

        assertEquals(List.of(first, third), findByPatient.execute(patient1));
        assertEquals(List.of(second), findByPatient.execute(patient2));
    }

    @Test
    void findByClinician_shouldReturnOnlyThatCliniciansAppointments() {
        Appointment first = appointment(1L, patient1, clinician1, LocalDate.of(2026, 9, 1), 10, 0, 10, 15);
        Appointment second = appointment(2L, patient2, clinician1, LocalDate.of(2026, 9, 1), 10, 15, 10, 30);
        Appointment third = appointment(3L, patient1, clinician2, LocalDate.of(2026, 9, 2), 11, 0, 11, 15);
        createAppointment.execute(first);
        createAppointment.execute(second);
        createAppointment.execute(third);

        assertEquals(List.of(first, second), findByClinician.execute(clinician1));
        assertEquals(List.of(third), findByClinician.execute(clinician2));
    }

    @Test
    void getAppointments_shouldReturnUnmodifiableSnapshot() {
        Appointment appointment = appointment(1L, patient1, clinician1, LocalDate.of(2026, 9, 1), 10, 0, 10, 15);
        createAppointment.execute(appointment);

        List<Appointment> result = getAppointments.execute();

        assertThrows(UnsupportedOperationException.class, () -> result.add(appointment));
        assertEquals(1, repository.getAppointments().size());
    }

    private Appointment appointment(
            Long id,
            Patient patient,
            Clinician clinician,
            LocalDate date,
            int startHour,
            int startMinute,
            int endHour,
            int endMinute) {

        return new Appointment(
                id,
                patient,
                clinician,
                date,
                new TimeRange(
                        LocalTime.of(startHour, startMinute),
                        LocalTime.of(endHour, endMinute)
                ),
                clinician.getDepartment()
        );
    }

    private Patient patient(Long id, String nationalId, String name, String surname) {
        User user = user(id, nationalId, name, surname, UserRole.PATIENT);
        return new Patient(user, BloodType.A_POSITIVE, new ArrayList<>());
    }

    private Clinician clinician(
            Long id,
            String nationalId,
            String name,
            String surname,
            String employeeId,
            ClinicalDepartment department) {

        User user = user(id, nationalId, name, surname, UserRole.CLINICIAN);
        Staff staff = new Staff(
                employeeId,
                LocalDate.of(2020, 1, 1),
                EmploymentStatus.ACTIVE,
                user
        );

        return new Clinician(staff, department);
    }

    private User user(Long id, String nationalId, String name, String surname, UserRole role) {
        return new User(
                id,
                new NationalId(nationalId),
                name,
                surname,
                LocalDate.of(1990, 1, 1),
                name.toLowerCase() + "@example.com",
                "+905551234567",
                new ArrayList<>(List.of(role))
        );
    }
}

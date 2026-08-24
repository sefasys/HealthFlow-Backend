import appointment.*;
import clinicaldepartment.Cardiology;
import clinicaldepartment.ClinicalDepartment;
import user.NationalId;
import user.User;
import user.UserRole;
import user.patient.BloodType;
import user.patient.Patient;
import user.staff.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // 1) Patient User
        User patientUser = new User(
                1L,
                new NationalId("11111111112"),
                "Sefa",
                "Soysal",
                LocalDate.of(2004, 1, 1),
                "sefa@test.com",
                "05000000000",
                new ArrayList<UserRole>()
        );

        // 2) Patient
        Patient patient = new Patient(
                patientUser,
                BloodType.A_POSITIVE,
                new ArrayList<>()
        );

        // 3) Clinician User
        User clinicianUser = new User(
                2L,
                new NationalId("22222222222"),
                "Ahmet",
                "Doktor",
                LocalDate.of(1980, 5, 10),
                "doctor@test.com",
                "05000000001",
                new ArrayList<UserRole>()
        );

        // 4) Staff
        Staff staff = new Staff(
                "EMP-001",
                LocalDate.of(2020, 1, 1),
                EmploymentStatus.ACTIVE,
                clinicianUser
        );

        // 5) Department
        ClinicalDepartment department = new Cardiology();

        // 6) Clinician
        Clinician clinician = new Clinician(
                staff,
                department
        );

        // 7) Availability
        LocalDate appointmentDate =
                LocalDate.of(2026, 8, 25);

        Availability availability =
                new Availability(
                        clinician,
                        appointmentDate,
                        new TimeRange(
                                LocalTime.of(10, 0),
                                LocalTime.of(12, 0)
                        )
                );

        clinician.addAvailability(availability);

        // 8) Scheduler
        AppointmentScheduler scheduler =
                new AppointmentScheduler();

        // 9) Registrar staff
        User registrarUser = new User(
                3L,
                new NationalId("33333333334"),
                "Mehmet",
                "Registrar",
                LocalDate.of(1990, 3, 15),
                "registrar@test.com",
                "05000000002",
                new ArrayList<UserRole>()
        );

        Staff registrarStaff = new Staff(
                "REG-001",
                LocalDate.of(2023, 1, 1),
                EmploymentStatus.ACTIVE,
                registrarUser
        );

        ClinicRegistrar registrar =
                new ClinicRegistrar(
                        registrarStaff,
                        scheduler
                );

        // 10) Global appointment list
        List<Appointment> appointments =
                new ArrayList<>();

        // 11) Patient selects a valid range
        TimeRange requestedRange =
                new TimeRange(
                        LocalTime.of(10, 15),
                        LocalTime.of(10, 30)
                );

        // 12) Create appointment
        Appointment appointment =
                registrar.createAppointment(
                        patient,
                        clinician,
                        availability,
                        requestedRange,
                        appointments
                );

        // 13) Print results
        System.out.println("Appointment created successfully.");
        System.out.println(
                "Status: " + appointment.getStatus()
        );
        System.out.println(
                "Date: " + appointment.getDate()
        );
        System.out.println(
                "Time: " + appointment.getTimeRange()
        );
        System.out.println(
                "Global appointment count: "
                        + appointments.size()
        );
        System.out.println(
                "Patient appointment count: "
                        + patient.getAppointments().size()
        );

        User secondPatientUser = new User(
                4L,
                new NationalId("44444444444"),
                "Ali",
                "Yilmaz",
                LocalDate.of(1998, 6, 10),
                "ali@test.com",
                "05000000003",
                new ArrayList<UserRole>()
        );

        Patient secondPatient = new Patient(
                secondPatientUser,
                BloodType.B_POSITIVE,
                new ArrayList<>()
        );


        System.out.println("\n--- Update Conflict Test ---");

// İlk patient için başlangıç appointment'ı
        TimeRange firstRange =
                new TimeRange(
                        LocalTime.of(10, 30),
                        LocalTime.of(10, 45)
                );

        Appointment firstAppointment =
                registrar.createAppointment(
                        patient,
                        clinician,
                        availability,
                        firstRange,
                        appointments
                );

// İkinci patient dolu olacak slotu alıyor
        TimeRange occupiedRange =
                new TimeRange(
                        LocalTime.of(10, 45),
                        LocalTime.of(11, 0)
                );

        Appointment secondAppointment =
                registrar.createAppointment(
                        secondPatient,
                        clinician,
                        availability,
                        occupiedRange,
                        appointments
                );

        System.out.println(
                "First appointment: "
                        + firstAppointment.getTimeRange()
        );

        System.out.println(
                "Second appointment: "
                        + secondAppointment.getTimeRange()
        );

// İlk appointment'ı ikinci appointment'ın saatine taşımayı deniyoruz
        try {
            registrar.updateAppointment(
                    firstAppointment,
                    availability,
                    occupiedRange,
                    appointments
            );

            System.out.println(
                    "ERROR: Conflicting update was allowed!"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Conflicting update correctly blocked: "
                            + e.getMessage()
            );
        }

        System.out.println(
                "First appointment after failed update: "
                        + firstAppointment.getTimeRange()
        );

        System.out.println(
                "Global appointment count: "
                        + appointments.size()
        );


        System.out.println("\n--- SlotGenerator Test ---");

        SlotGenerator slotGenerator = new SlotGenerator();

        Availability slotTestAvailability =
                new Availability(
                        clinician,
                        LocalDate.of(2026, 8, 26),
                        new TimeRange(
                                LocalTime.of(10, 0),
                                LocalTime.of(11, 0)
                        )
                );

        List<Appointment> slotTestAppointments =
                new ArrayList<>();

        Appointment bookedAppointment =
                new Appointment(
                        patient,
                        clinician,
                        slotTestAvailability.getDate(),
                        new TimeRange(
                                LocalTime.of(10, 15),
                                LocalTime.of(10, 30)
                        ),
                        clinician.getDepartment()
                );

        slotTestAppointments.add(bookedAppointment);

        List<AppointmentSlot> slots =
                slotGenerator.generateSlots(
                        slotTestAvailability,
                        Duration.ofMinutes(15),
                        slotTestAppointments
                );

        for (AppointmentSlot slot : slots) {
            System.out.println(
                    slot.timeRange()
                            + " -> "
                            + slot.status()
            );
        }
    }
}
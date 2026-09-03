package com.healthflow.application.usecase.patient;

import static org.junit.jupiter.api.Assertions.*;

import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.patient.BloodType;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.infrastructure.repository.InMemoryPatientRepository;
import com.healthflow.port.repository.PatientRepository;
import com.healthflow.port.repository.PatientSortType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PatientUseCasesTest {

  private PatientRepository repository;
  private CreatePatientUseCase createPatient;
  private GetPatientsUseCase getPatients;
  private FindPatientByUniqueIdUseCase findByUniqueId;
  private FindPatientByNationalIdUseCase findByNationalId;
  private SearchPatientUseCase searchPatient;
  private SortPatientsUseCase sortPatients;

  @BeforeEach
  void setUp() {
    repository = new InMemoryPatientRepository(new ArrayList<>());
    createPatient = new CreatePatientUseCase(repository);
    getPatients = new GetPatientsUseCase(repository);
    findByUniqueId = new FindPatientByUniqueIdUseCase(repository);
    findByNationalId = new FindPatientByNationalIdUseCase(repository);
    searchPatient = new SearchPatientUseCase(repository);
    sortPatients = new SortPatientsUseCase(repository);
  }

  @Test
  void createPatient_shouldAddPatientToRepository() {
    Patient patient = patient(1L, "12345678902", "Sefa", "Soysal", LocalDate.of(2004, 1, 10));

    Patient result = createPatient.execute(patient);

    assertSame(patient, result);
    assertEquals(1, getPatients.execute().size());
    assertSame(patient, getPatients.execute().getFirst());
  }

  @Test
  void createPatient_shouldRejectSameNationalIdObjectTwice() {
    NationalId nationalId = new NationalId("12345678902");
    Patient first = patient(1L, nationalId, "Sefa", "Soysal", LocalDate.of(2004, 1, 10));
    Patient second = patient(2L, nationalId, "Ali", "Yilmaz", LocalDate.of(2001, 5, 20));

    createPatient.execute(first);

    assertThrows(IllegalArgumentException.class, () -> createPatient.execute(second));
    assertEquals(1, getPatients.execute().size());
  }

  @Test
  void findByUniqueId_shouldReturnMatchingPatient() {
    Patient patient = patient(15L, "12345678902", "Sefa", "Soysal", LocalDate.of(2004, 1, 10));
    createPatient.execute(patient);

    var result = findByUniqueId.execute(15L);

    assertTrue(result.isPresent());
    assertSame(patient, result.get());
  }

  @Test
  void findByUniqueId_shouldReturnEmptyWhenPatientDoesNotExist() {
    assertTrue(findByUniqueId.execute(999L).isEmpty());
  }

  @Test
  void findByNationalId_shouldReturnMatchingPatient() {
    Patient patient = patient(1L, "12345678902", "Sefa", "Soysal", LocalDate.of(2004, 1, 10));
    createPatient.execute(patient);

    var result = findByNationalId.execute(patient.getUser().getNationalId());

    assertTrue(result.isPresent());
    assertSame(patient, result.get());
  }

  @Test
  void search_shouldBeCaseInsensitiveAndSearchNameOrSurname() {
    Patient sefa = patient(1L, "12345678902", "Sefa", "Soysal", LocalDate.of(2004, 1, 10));
    Patient ali = patient(2L, "12345678904", "Ali", "Kaya", LocalDate.of(2000, 2, 20));
    createPatient.execute(sefa);
    createPatient.execute(ali);

    assertEquals(List.of(sefa), searchPatient.execute("sEf"));
    assertEquals(List.of(ali), searchPatient.execute("KAY"));
  }

  @Test
  void search_shouldRejectNullAndBlankQueries() {
    assertThrows(IllegalArgumentException.class, () -> searchPatient.execute(null));
    assertThrows(IllegalArgumentException.class, () -> searchPatient.execute("   "));
  }

  @Test
  void sort_shouldSortByNameSurnameAndBirthDate() {
    Patient zeynep = patient(1L, "12345678902", "Zeynep", "Acar", LocalDate.of(2003, 6, 15));
    Patient ali = patient(2L, "12345678904", "Ali", "Yilmaz", LocalDate.of(1999, 1, 5));
    Patient mehmet = patient(3L, "12345678906", "Mehmet", "Kaya", LocalDate.of(2001, 3, 10));
    createPatient.execute(zeynep);
    createPatient.execute(ali);
    createPatient.execute(mehmet);

    assertEquals(List.of(ali, mehmet, zeynep), sortPatients.execute(PatientSortType.NAME_ASC));
    assertEquals(List.of(zeynep, mehmet, ali), sortPatients.execute(PatientSortType.NAME_DESC));
    assertEquals(List.of(zeynep, mehmet, ali), sortPatients.execute(PatientSortType.SURNAME_ASC));
    assertEquals(List.of(ali, mehmet, zeynep), sortPatients.execute(PatientSortType.SURNAME_DESC));
    assertEquals(
        List.of(ali, mehmet, zeynep), sortPatients.execute(PatientSortType.BIRTH_DATE_ASC));
    assertEquals(
        List.of(zeynep, mehmet, ali), sortPatients.execute(PatientSortType.BIRTH_DATE_DESC));
  }

  @Test
  void getPatients_shouldReturnUnmodifiableSnapshot() {
    Patient patient = patient(1L, "12345678902", "Sefa", "Soysal", LocalDate.of(2004, 1, 10));
    createPatient.execute(patient);

    List<Patient> result = getPatients.execute();

    assertThrows(UnsupportedOperationException.class, () -> result.add(patient));
    assertEquals(1, repository.getPatients().size());
  }

  @Test
  void createPatient_shouldRejectDifferentNationalIdObjectsWithSameValue() {
    Patient first = patient(1L, "12345678902", "Sefa", "Soysal", LocalDate.of(2004, 1, 10));
    Patient second = patient(2L, "12345678902", "Ali", "Kaya", LocalDate.of(2000, 2, 20));

    createPatient.execute(first);

    assertThrows(IllegalArgumentException.class, () -> createPatient.execute(second));
  }

  private Patient patient(
      Long id, String nationalId, String name, String surname, LocalDate birthDate) {
    return patient(id, new NationalId(nationalId), name, surname, birthDate);
  }

  private Patient patient(
      Long id, NationalId nationalId, String name, String surname, LocalDate birthDate) {
    User user =
        new User(
            id,
            nationalId,
            name,
            surname,
            birthDate,
            name.toLowerCase() + "@example.com",
            "+905551234567",
            new ArrayList<>(List.of(UserRole.PATIENT)));

    return new Patient(user, BloodType.A_POSITIVE, new ArrayList<>());
  }
}

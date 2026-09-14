package com.healthflow.infrastructure.repository;

import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import com.healthflow.port.repository.PatientSortType;

import java.util.*;

public class InMemoryIPatientRepository implements IPatientRepository {
  private final List<Patient> patients;

  public InMemoryIPatientRepository() {
    patients = new ArrayList<>();
  }

  public List<Patient> getPatients() {
    return patients.stream().toList(); // bu arada getPatients kısmında kopyasını almayı düşündüm
    // ilk yani List<Patient> copyPatients = patients.stream().toList(); şeklined düşünüp onu return
    // etmiştim ama gereksiz dedi.
    // bu şekilde yeni ve değiştirilemez liste döndürüyor.
  }

  public void addPatient(Patient patient) {
    patients.add(patient);
  }

  public List<Patient> sort(PatientSortType sortType) {
    return switch (sortType) {
      case NAME_ASC ->
          patients.stream()
              .sorted(Comparator.comparing(patient -> patient.getUser().getName()))
              .toList();
      case NAME_DESC ->
          patients.stream()
              .sorted(
                  Comparator.comparing((Patient patient) -> patient.getUser().getName()).reversed())
              .toList();
      case BIRTH_DATE_ASC ->
          patients.stream()
              .sorted(Comparator.comparing(patient -> patient.getUser().getBirthDate()))
              .toList();
      case BIRTH_DATE_DESC ->
          patients.stream()
              .sorted(
                  Comparator.comparing((Patient patient) -> patient.getUser().getBirthDate())
                      .reversed())
              .toList();
      case SURNAME_ASC ->
          patients.stream()
              .sorted(Comparator.comparing(patient -> patient.getUser().getSurname()))
              .toList();
      case SURNAME_DESC ->
          patients.stream()
              .sorted(
                  Comparator.comparing((Patient patient) -> patient.getUser().getSurname())
                      .reversed())
              .toList();
    };
  } // İlk olarak klasik switch ile yazdım sonrasında IntelliJ'in önerisi üzerine modern switch

  // yapısına geçtim.

  // GPT güzel bir öneri verdi dönüş tipini optional yap bence dedi çünkü aradığın find metotlarında
  // aradığın kullanıcı bulunamayabilir de.
  public Optional<Patient> findByUniqueId(UUID uniqueId) {
    if (uniqueId != null) {
      return patients.stream()
          .filter(patient -> patient.getUser().getUniqueID().equals(uniqueId))
          .findFirst();
    } else return Optional.empty(); // exception da verilebilir burada belki.
  }

  public Optional<Patient> findByNationalId(NationalId nationalId) {
    if (nationalId != null) {
      return patients.stream()
          .filter(patient -> patient.getUser().getNationalId().equals(nationalId))
          .findFirst();
    } else return Optional.empty();
  }

  public List<Patient> search(String query) { // query sorgu demek unutma
    if (query == null) throw new IllegalArgumentException("Search query cannot be null.");
    if (query.isBlank()) {
      throw new IllegalArgumentException("Search query is blank.");
    }
    String normalizedQuery = query.toLowerCase();
    return patients.stream()
        .filter(
            patient ->
                (patient.getUser().getName().toLowerCase().contains(normalizedQuery)
                    || patient.getUser().getSurname().toLowerCase().contains(normalizedQuery)))
        .toList();
  }



}

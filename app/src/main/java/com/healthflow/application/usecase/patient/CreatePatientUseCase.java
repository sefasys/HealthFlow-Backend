package com.healthflow.application.usecase.patient;

import com.healthflow.application.exception.PatientAlreadyExistsException;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.domain.model.user.UserRole;
import com.healthflow.domain.model.user.patient.Patient;
import com.healthflow.port.repository.IPatientRepository;
import java.time.LocalDate;

public class CreatePatientUseCase {

  private final IPatientRepository patientRepository;
  private final UserFactory userFactory;

  public CreatePatientUseCase(IPatientRepository patientRepository, UserFactory userFactory) {
    this.patientRepository = patientRepository;
    this.userFactory = userFactory;
  }

  public Patient execute(
      NationalId nationalId,
      String name,
      String surname,
      LocalDate birthDate,
      String email,
      String phoneNumber) {

    if (patientRepository.findByNationalId(nationalId).isPresent()) {
      throw new PatientAlreadyExistsException(
          "There is already a user with the same national identity number.");
    }

    User user =
        userFactory.createUser(
            nationalId, name, surname, birthDate, email, phoneNumber, UserRole.PATIENT);
    Patient patient = new Patient(user);
    patientRepository.addPatient(patient);
    return patient;
  }
}

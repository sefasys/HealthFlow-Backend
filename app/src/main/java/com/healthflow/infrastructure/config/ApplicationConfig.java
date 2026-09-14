package com.healthflow.infrastructure.config;

import com.healthflow.application.usecase.patient.CreatePatientUseCase;
import com.healthflow.application.usecase.patient.GetPatientsUseCase;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.infrastructure.repository.InMemoryIPatientRepository;
import com.healthflow.port.repository.IPatientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
  @Bean
  public UserFactory userFactory() {
    return new UserFactory();
  }

  @Bean
  public IPatientRepository patientRepository() {
    return new InMemoryIPatientRepository();
  }

  @Bean
  public CreatePatientUseCase createPatientUseCase(
      IPatientRepository iPatientRepository, UserFactory userFactory) {
    return new CreatePatientUseCase(iPatientRepository, userFactory);
  }

  @Bean
  public GetPatientsUseCase getPatientsUseCase(IPatientRepository patientRepository) {
    return new GetPatientsUseCase(patientRepository);
  }
}

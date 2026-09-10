package com.healthflow.infrastructure.config;

import com.healthflow.application.usecase.patient.CreatePatientUseCase;
import com.healthflow.domain.factory.UserFactory;
import com.healthflow.infrastructure.repository.InMemoryPatientRepository;
import com.healthflow.port.repository.PatientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public UserFactory userFactory(){
        return new UserFactory();
    }

    @Bean
    public PatientRepository patientRepository(){
        return new InMemoryPatientRepository();
    }

    @Bean
    public CreatePatientUseCase createPatientUseCase(
            PatientRepository patientRepository,
            UserFactory userFactory
    ){
        return new CreatePatientUseCase(patientRepository, userFactory);
    }

}

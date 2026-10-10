package com.healthflow.infrastructure.config;

import com.healthflow.domain.service.AppointmentScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeConfiguration {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Europe/Istanbul"));
    }

    @Bean
    public AppointmentScheduler appointmentScheduler(Clock clock) {
        return new AppointmentScheduler(clock);
    }
}
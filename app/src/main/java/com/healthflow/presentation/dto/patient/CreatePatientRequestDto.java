package com.healthflow.presentation.dto.patient;


import com.healthflow.domain.model.user.NationalId;

import java.time.LocalDate;


public record CreatePatientRequestDto(
        String nationalId, //Burada bilerek String olarak yazdık çünkü DTO’da String, domain’de NationalId tercih ederim. Çünkü DTO HTTP modelidir; client düz JSON gönderir. Domain value object’i dış API contract’ına sızdırmayalım.
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        String phoneNumber
) {
}

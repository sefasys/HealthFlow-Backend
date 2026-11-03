package com.healthflow.dto.clinician;

import jakarta.validation.constraints.NotBlank;

public record FindClinicianByNationalIdRequestDto(@NotBlank String nationalId) {// Bu ve FindPatientByNationalIdRequestDto class'ları user şeklinde birleştirilerek kodlar daha derli toplu hale getirilebilir.


}

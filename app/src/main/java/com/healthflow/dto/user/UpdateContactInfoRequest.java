package com.healthflow.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateContactInfoRequest(
        @NotBlank @Email String email,
        @Pattern(regexp = "^\\+?\\d{10,13}$") String phoneNumber
) {}

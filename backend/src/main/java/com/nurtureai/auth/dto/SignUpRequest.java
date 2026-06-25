package com.nurtureai.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
    @NotBlank @Size(min = 3, max = 40) String username,
    @NotBlank @Size(max = 80) String firstName,
    @NotBlank @Size(max = 80) String lastName,
    @NotBlank @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$") String phoneNumber,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 12, max = 128) String password
) {
}

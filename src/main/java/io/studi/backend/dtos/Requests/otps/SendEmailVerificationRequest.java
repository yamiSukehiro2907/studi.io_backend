package io.studi.backend.dtos.Requests.otps;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SendEmailVerificationRequest(
        @NotNull(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {
}

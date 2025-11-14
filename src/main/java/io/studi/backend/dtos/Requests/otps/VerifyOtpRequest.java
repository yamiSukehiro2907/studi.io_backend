package io.studi.backend.dtos.Requests.otps;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record VerifyOtpRequest(
        @NotNull(message = "Email is required")
        @Email(message = "Invalid format")
        String email,

        @NotNull(message = "Otp is required")
        String otp
) {
}

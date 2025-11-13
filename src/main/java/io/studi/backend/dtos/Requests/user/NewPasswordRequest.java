package io.studi.backend.dtos.Requests.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewPasswordRequest(
        @NotBlank(message = "Email is required")
        @NotNull(message = "Email cannot be null")
        String email,

        @NotBlank(message = "Password is required")
        @NotNull(message = "Password cannot be null")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password
) {
}

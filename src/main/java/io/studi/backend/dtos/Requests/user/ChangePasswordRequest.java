package io.studi.backend.dtos.Requests.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Old Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String currentPassword,
        @NotBlank(message = "New Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String newPassword
) {

}

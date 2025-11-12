package io.studi.backend.dtos.Requests.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email or Username is required")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;
}

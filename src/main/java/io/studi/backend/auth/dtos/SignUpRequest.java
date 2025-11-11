package io.studi.backend.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid Email format")
    @NotBlank
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}

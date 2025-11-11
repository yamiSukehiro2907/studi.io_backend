package io.studi.backend.auth.dtos.Responses;

import lombok.Data;

@Data
public class LoginResponse {

    private String userId;

    private String email;

    private String username;
}

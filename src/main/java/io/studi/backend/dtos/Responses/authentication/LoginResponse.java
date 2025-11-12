package io.studi.backend.dtos.Responses.authentication;

import lombok.Data;

@Data
public class LoginResponse {

    private String userId;

    private String email;

    private String username;
}

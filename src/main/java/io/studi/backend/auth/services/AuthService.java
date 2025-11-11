package io.studi.backend.auth.services;

import io.studi.backend.auth.dtos.SignUpRequest;
import io.studi.backend.auth.dtos.UserDto;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface AuthService {
    ResponseEntity<?> createUser(SignUpRequest signUpRequest);
}

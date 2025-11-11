package io.studi.backend.auth.services;

import io.studi.backend.auth.dtos.Requests.LoginRequest;
import io.studi.backend.auth.dtos.Requests.SignUpRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {
    ResponseEntity<?> createUser(SignUpRequest signUpRequest);

    ResponseEntity<?> loginUser(LoginRequest loginRequest, Authentication authentication, HttpServletResponse response);

    ResponseEntity<?> logOutUser(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<?> refreshUser(HttpServletRequest request, HttpServletResponse response);
}

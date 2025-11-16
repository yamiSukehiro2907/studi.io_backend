package io.studi.backend.services.authentication;

import io.studi.backend.dtos.Requests.authentication.SignUpRequest;
import io.studi.backend.dtos.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {
    ResponseEntity<ApiResponse<?>> createUser(SignUpRequest signUpRequest);

    ResponseEntity<ApiResponse<?>> loginUser(Authentication authentication, HttpServletResponse response);

    ResponseEntity<ApiResponse<?>> logOutUser(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<ApiResponse<?>> refreshUser(HttpServletRequest request, HttpServletResponse response);
}

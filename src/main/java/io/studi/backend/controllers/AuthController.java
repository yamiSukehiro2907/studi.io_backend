package io.studi.backend.controllers;

import io.studi.backend.dtos.Requests.authentication.LoginRequest;
import io.studi.backend.dtos.Requests.authentication.SignUpRequest;
import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.services.authentication.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        SignUpRequest cleanedRequest = new SignUpRequest(
                signUpRequest.name().trim(),
                signUpRequest.email().trim(),
                signUpRequest.password().trim()
        );

        if (cleanedRequest.password().length() < 6) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Password should be at least 6 characters long"));
        }
        return authService.createUser(cleanedRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.identifier(),
                        loginRequest.password()
                )
        );
        return authService.loginUser(auth, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logOut(HttpServletRequest request, HttpServletResponse response) {
        return authService.logOutUser(request, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshUser(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshUser(request, response);
    }
}

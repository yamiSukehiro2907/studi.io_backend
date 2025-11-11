package io.studi.backend.auth.controllers;

import io.studi.backend.auth.dtos.SignUpRequest;
import io.studi.backend.auth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    public final AuthService authService;

    public AuthController(AuthService _authService) {
        this.authService = _authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        ///  trimming the passed values
        signUpRequest.setEmail(signUpRequest.getEmail().trim());
        signUpRequest.setPassword(signUpRequest.getPassword().trim());
        signUpRequest.setName(signUpRequest.getName().trim());

        ///  checking for password length to make sure that it is 6 characters long
        if (signUpRequest.getPassword().length() < 6) {
            return new ResponseEntity<>(Map.of("message", "Password should be at least 6 characters long"), HttpStatus.BAD_REQUEST);
        }

        return authService.createUser(signUpRequest);
    }
}

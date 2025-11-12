package io.studi.backend.controllers;

import io.studi.backend.dtos.Requests.LoginRequest;
import io.studi.backend.dtos.Requests.SignUpRequest;
import io.studi.backend.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    public final AuthService authService;

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService _authService, AuthenticationManager _authenticationManager) {
        this.authService = _authService;
        this.authenticationManager = _authenticationManager;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        return authService.loginUser(loginRequest, auth, response);
    }

    @PostMapping("/logOut")
    public ResponseEntity<?> logOut(HttpServletRequest request, HttpServletResponse response) {
        return authService.logOutUser(request, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshUser(HttpServletRequest request, HttpServletResponse response){
        return authService.refreshUser(request , response);
    }
}

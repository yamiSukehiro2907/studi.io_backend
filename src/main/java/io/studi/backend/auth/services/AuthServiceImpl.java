package io.studi.backend.auth.services;

import io.studi.backend.auth.dtos.Requests.LoginRequest;
import io.studi.backend.auth.dtos.Requests.SignUpRequest;
import io.studi.backend.auth.dtos.Responses.LoginResponse;
import io.studi.backend.auth.dtos.Responses.UserDto;
import io.studi.backend.auth.models.User;
import io.studi.backend.auth.repositories.AuthRepository;
import io.studi.backend.auth.security.CustomUserDetails;
import io.studi.backend.common.utils.Helper;
import io.studi.backend.common.utils.JwtUtil;
import io.studi.backend.common.utils.LoggerHelper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtUtil _jwtUtil) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = _jwtUtil;
    }

    @Override
    public ResponseEntity<?> createUser(SignUpRequest signUpRequest) {
        try {
            LoggerHelper.info(this, "Attempting to create user with email: " + signUpRequest.getEmail());

            Optional<User> alreadyExistingUser = authRepository.findByEmail(signUpRequest.getEmail());
            if (alreadyExistingUser.isPresent()) {
                LoggerHelper.warn(this, "Email already exists: " + signUpRequest.getEmail());
                return new ResponseEntity<>(Map.of(
                        "message", "Email already exists"
                ), HttpStatus.CONFLICT);
            }

            ///  Creating a new user
            User user = new User();

            /// encoding the password
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setEmail(signUpRequest.getEmail());
            user.setName(signUpRequest.getName());
            user.setUsername(Helper.generateUsername());

            LoggerHelper.debug(this, "User details prepared. Saving to database...");

            User createdUser = authRepository.createUser(user);
            LoggerHelper.info(this, "User created successfully: " + createdUser.getId());

            UserDto userDto = Helper.getUserDto(createdUser);
            return new ResponseEntity<>(userDto, HttpStatus.CREATED);

        } catch (Exception e) {
            LoggerHelper.error(this, "Error while creating user: " + e.getMessage(), e);
            return new ResponseEntity<>(Map.of(
                    "message", "Internal Server Error"
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> loginUser(LoginRequest loginRequest, Authentication authentication, HttpServletResponse response) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            String accessToken = jwtUtil.generateAccessToken(userDetails);

            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            Cookie accessCookie = new Cookie("accessToken", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(60 * 60);

            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            accessCookie.setMaxAge(7 * 24 * 60 * 60);

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);

            user.setRefreshToken(refreshToken);

            authRepository.save(user);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setUserId(user.getId());
            loginResponse.setUsername(user.getUsername());
            loginResponse.setEmail(user.getEmail());
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } catch (Exception e) {
            LoggerHelper.error(this, "Error while creating user: " + e.getMessage(), e);
            return new ResponseEntity<>(Map.of(
                    "message", "Internal Server Error"
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

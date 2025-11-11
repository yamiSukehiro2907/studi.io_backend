package io.studi.backend.auth.services;

import io.studi.backend.auth.dtos.SignUpRequest;
import io.studi.backend.auth.dtos.UserDto;
import io.studi.backend.auth.models.User;
import io.studi.backend.auth.repositories.AuthRepository;
import io.studi.backend.common.utils.Helper;
import io.studi.backend.common.utils.LoggerHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
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
}

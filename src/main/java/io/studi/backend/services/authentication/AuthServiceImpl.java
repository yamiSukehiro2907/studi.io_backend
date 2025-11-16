package io.studi.backend.services.authentication;

import io.studi.backend.dtos.Requests.authentication.SignUpRequest;
import io.studi.backend.dtos.users.LoginResponse;
import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.helpers.AuthHelper;
import io.studi.backend.helpers.UserHelper;
import io.studi.backend.models.User;
import io.studi.backend.repositories.user.UserRepository;
import io.studi.backend.security.CustomUserDetails;
import io.studi.backend.utils.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<ApiResponse<?>> createUser(SignUpRequest signUpRequest) {
        try {
            if (userRepository.findByEmail(signUpRequest.email()) != null) {
                log.warn("Attempt to register with existing email: {}", signUpRequest.email());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("Email already exists"));
            }

            String username;
            do {
                username = UserHelper.generateUsername();
            } while (userRepository.loadUserByUsername(username) != null);

            User user = new User();
            user.setPassword(passwordEncoder.encode(signUpRequest.password()));
            user.setEmail(signUpRequest.email());
            user.setName(signUpRequest.name());
            user.setUsername(username);
            userRepository.createUser(user);

            log.info("User registered successfully: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User created successfully", null));

        } catch (Exception e) {
            log.error("Error while creating user", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> loginUser(Authentication authentication, HttpServletResponse response) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            response.addCookie(AuthHelper.createAccessTokenCookie(accessToken, 3600));
            response.addCookie(AuthHelper.createRefreshTokenCookie(refreshToken, 7 * 24 * 3600));

            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            LoginResponse loginResponse = new LoginResponse(user.getId(), user.getUsername(), user.getEmail());

            log.info("User logged in successfully: {}", user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));

        } catch (Exception e) {
            log.error("Error while logging in", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> logOutUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (request.getCookies() != null) {
                String accessToken = AuthHelper.getAccessTokenFromHttpRequest(request);
                if (accessToken != null) {
                    String userId = jwtUtil.getIdAccessToken(accessToken);
                    User user = userRepository.loadUserById(userId);
                    if (user != null) {
                        user.setRefreshToken("");
                        userRepository.save(user);
                        log.info("Access token cleared for user {}", userId);
                    }
                }

                String refreshToken = AuthHelper.getRefreshTokenFromHttpRequest(request);
                if (refreshToken != null) {
                    String userId = jwtUtil.getIdRefreshToken(refreshToken);
                    User user = userRepository.loadUserById(userId);
                    if (user != null) {
                        user.setRefreshToken("");
                        userRepository.save(user);
                        log.info("Refresh token cleared for user {}", userId);
                    }
                }
            }

            response.addCookie(AuthHelper.createAccessTokenCookie(null, 0));
            response.addCookie(AuthHelper.createRefreshTokenCookie(null, 0));

            return ResponseEntity.ok(ApiResponse.success("Logout successful", null));

        } catch (Exception e) {
            log.error("Error during logout", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> refreshUser(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Token not provided"));
        }

        try {
            String refreshToken = AuthHelper.getRefreshTokenFromHttpRequest(request);

            if (refreshToken == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Token not provided"));
            }

            if (!jwtUtil.isValidRefreshToken(refreshToken)) {
                log.warn("Invalid refresh token detected");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid token"));
            }

            String userId = jwtUtil.getIdRefreshToken(refreshToken);
            User user = userRepository.loadUserById(userId);

            if (user == null) {
                log.warn("User not found during token refresh");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("User not found"));
            }

            if (!refreshToken.equals(user.getRefreshToken())) {
                log.warn("Mismatched refresh token for user {}", user.getId());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid token"));
            }

            CustomUserDetails userDetails = new CustomUserDetails(user);

            String newAccessToken = jwtUtil.generateAccessToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            response.addCookie(AuthHelper.createAccessTokenCookie(newAccessToken, 3600));
            response.addCookie(AuthHelper.createRefreshTokenCookie(newRefreshToken, 7 * 24 * 3600));

            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);

            log.info("User {} refreshed successfully", user.getId());
            return ResponseEntity.ok(ApiResponse.success("User refreshed successfully", null));

        } catch (Exception e) {
            log.error("Error during token refresh", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error"));
        }
    }
}

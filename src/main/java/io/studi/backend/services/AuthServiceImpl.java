package io.studi.backend.services;

import io.studi.backend.dtos.Requests.LoginRequest;
import io.studi.backend.dtos.Requests.SignUpRequest;
import io.studi.backend.dtos.Responses.LoginResponse;
import io.studi.backend.dtos.Responses.UserDto;
import io.studi.backend.models.User;
import io.studi.backend.repositories.AuthRepository;
import io.studi.backend.security.CustomUserDetails;
import io.studi.backend.constants.Helper;
import io.studi.backend.utils.JwtUtil;
import io.studi.backend.constants.LoggerHelper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
                return new ResponseEntity<>(Map.of(
                        "message", "Email already exists"
                ), HttpStatus.CONFLICT);
            }

            String username = null;

            while (true) {
                String temp = Helper.generateUsername();
                User user = authRepository.loadUserByUsername(temp);
                if (user == null) {
                    username = temp;
                    break;
                }
            }

            ///  Creating a new user
            User user = new User();

            /// encoding the password
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setEmail(signUpRequest.getEmail());
            user.setName(signUpRequest.getName());
            user.setUsername(username);

            User createdUser = authRepository.createUser(user);

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
            refreshCookie.setMaxAge(7 * 24 * 60 * 60);

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

    @Override
    public ResponseEntity<?> logOutUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (request.getCookies() != null) {
                ///  if accessToken exists
                String accessToken = Helper.getAccessTokenFromHttpRequest(request);
                if (accessToken != null) {
                    String userId = jwtUtil.getIdAccessToken(accessToken);
                    User user = authRepository.loadUserById(userId);
                    if (user != null) {
                        user.setRefreshToken("");
                        authRepository.save(user);
                    }
                }

                ///  if refreshToken exists
                String refreshToken = Helper.getRefreshTokenFromHttpRequest(request);
                if (refreshToken != null) {
                    String userId = jwtUtil.getIdRefreshToken(refreshToken);
                    User user = authRepository.loadUserById(userId);
                    if (user != null) {
                        user.setRefreshToken("");
                        authRepository.save(user);
                    }
                }

                String JSESSIONID = Helper.getJSESSIONID(request);
                if (JSESSIONID != null) {
                    Cookie JSESSIONCookie = new Cookie("JSESSIONID", null);
                    JSESSIONCookie.setHttpOnly(true);
                    JSESSIONCookie.setSecure(true);
                    JSESSIONCookie.setPath("/");
                    JSESSIONCookie.setMaxAge(0);
                    response.addCookie(JSESSIONCookie);
                }
            }

            Cookie accessCookie = new Cookie("accessToken", null);
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(0);

            Cookie refreshCookie = new Cookie("refreshToken", null);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(0);

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);
        } catch (Exception e) {
            LoggerHelper.error(this, "Error logging Out: ", e);
        }

        return new ResponseEntity<>(Map.of("message", "LogOut Successful!"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> refreshUser(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() == null) {
            return new ResponseEntity<>(Map.of("message", "Token not provided"), HttpStatus.BAD_REQUEST);
        }
        try {
            ///  if refreshToken exists
            String refreshToken = Helper.getRefreshTokenFromHttpRequest(request);

            if (refreshToken == null) {
                return new ResponseEntity<>(Map.of("message", "Token not provided"), HttpStatus.BAD_REQUEST);
            }

            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                return new ResponseEntity<>(Map.of("message", "Invalid Token"), HttpStatus.CONFLICT);
            }

            String userId = jwtUtil.getIdRefreshToken(refreshToken);
            User user = authRepository.loadUserById(userId);

            if (user == null) {
                return new ResponseEntity<>(Map.of("message", "User Not Found"), HttpStatus.NOT_FOUND);
            }

            if (!user.getRefreshToken().equals(refreshToken)) {
                return new ResponseEntity<>(Map.of("message", "Invalid Token"), HttpStatus.CONFLICT);
            }

            CustomUserDetails userDetails = new CustomUserDetails(user);

            String accessToken = jwtUtil.generateAccessToken(userDetails);

            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            Cookie accessCookie = new Cookie("accessToken", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(60 * 60);

            Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60);

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);

            user.setRefreshToken(newRefreshToken);

            authRepository.save(user);

            return new ResponseEntity<>(Map.of("message", "User refreshed successfully!"), HttpStatus.OK);

        } catch (Exception e) {
            LoggerHelper.error(this, "Error logging Out: ", e);
            return new ResponseEntity<>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

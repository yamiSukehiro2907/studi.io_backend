package io.studi.backend.services.authentication;

import io.studi.backend.helpers.AuthHelper;
import io.studi.backend.helpers.LoggerHelper;
import io.studi.backend.helpers.UserHelper;
import io.studi.backend.dtos.Requests.authentication.LoginRequest;
import io.studi.backend.dtos.Requests.authentication.SignUpRequest;
import io.studi.backend.dtos.Responses.authentication.LoginResponse;
import io.studi.backend.models.User;
import io.studi.backend.repositories.userDetails.UserRepository;
import io.studi.backend.security.CustomUserDetails;
import io.studi.backend.utils.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil _jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = _jwtUtil;
    }

    @Override
    public ResponseEntity<?> createUser(SignUpRequest signUpRequest) {
        try {
            User alreadyExistingUser = userRepository.findByEmail(signUpRequest.getEmail());
            if (alreadyExistingUser != null) {
                return new ResponseEntity<>(Map.of("message", "Email already exists"), HttpStatus.CONFLICT);
            }

            String username = null;

            while (true) {
                String temp = UserHelper.generateUsername();
                User user = userRepository.loadUserByUsername(temp);
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

            userRepository.createUser(user);

            return new ResponseEntity<>(Map.of("message", "User created successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            LoggerHelper.error(this, "Error while creating user: " + e.getMessage(), e);
            return new ResponseEntity<>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> loginUser(LoginRequest loginRequest, Authentication authentication, HttpServletResponse response) {
        try {
            /// get user
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            ///  create tokens
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            ///  set tokens
            response.addCookie(AuthHelper.createAccessTokenCookie(accessToken, 60 * 60));
            response.addCookie(AuthHelper.createRefreshTokenCookie(refreshToken, 7 * 24 * 60 * 60));

            /// update refreshToken in userTable
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            /// generate loginResponse
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setUserId(user.getId());
            loginResponse.setUsername(user.getUsername());
            loginResponse.setEmail(user.getEmail());

            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } catch (Exception e) {
            LoggerHelper.error(this, "Error while creating user: " + e.getMessage(), e);
            return new ResponseEntity<>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> logOutUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (request.getCookies() != null) {
                ///  if accessToken exists
                String accessToken = AuthHelper.getAccessTokenFromHttpRequest(request);
                if (accessToken != null) {
                    String userId = jwtUtil.getIdAccessToken(accessToken);
                    User user = userRepository.loadUserById(userId);
                    if (user != null) {
                        user.setRefreshToken("");
                        userRepository.save(user);
                    }
                }

                ///  if refreshToken exists
                String refreshToken = AuthHelper.getRefreshTokenFromHttpRequest(request);
                if (refreshToken != null) {
                    String userId = jwtUtil.getIdRefreshToken(refreshToken);
                    User user = userRepository.loadUserById(userId);
                    if (user != null) {
                        user.setRefreshToken("");
                        userRepository.save(user);
                    }
                }
            }

            response.addCookie(AuthHelper.createAccessTokenCookie(null, 0));
            response.addCookie(AuthHelper.createRefreshTokenCookie(null, 0));
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
            String refreshToken = AuthHelper.getRefreshTokenFromHttpRequest(request);

            if (refreshToken == null) {
                return new ResponseEntity<>(Map.of("message", "Token not provided"), HttpStatus.BAD_REQUEST);
            }

            /// if refreshToken is expired / invalid
            if (!jwtUtil.isValidRefreshToken(refreshToken)) {
                return new ResponseEntity<>(Map.of("message", "Invalid Token"), HttpStatus.CONFLICT);
            }

            String userId = jwtUtil.getIdRefreshToken(refreshToken);
            User user = userRepository.loadUserById(userId);

            if (user == null) {
                return new ResponseEntity<>(Map.of("message", "User Not Found"), HttpStatus.NOT_FOUND);
            }

            if (!user.getRefreshToken().equals(refreshToken)) {
                return new ResponseEntity<>(Map.of("message", "Invalid Token"), HttpStatus.CONFLICT);
            }

            CustomUserDetails userDetails = new CustomUserDetails(user);

            String accessToken = jwtUtil.generateAccessToken(userDetails);

            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            response.addCookie(AuthHelper.createAccessTokenCookie(accessToken, 60 * 60));
            response.addCookie(AuthHelper.createRefreshTokenCookie(newRefreshToken, 7 * 24 * 60 * 60));

            user.setRefreshToken(newRefreshToken);

            userRepository.save(user);

            return new ResponseEntity<>(Map.of("message", "User refreshed successfully!"), HttpStatus.OK);

        } catch (Exception e) {
            LoggerHelper.error(this, "Error logging Out: ", e);
            return new ResponseEntity<>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

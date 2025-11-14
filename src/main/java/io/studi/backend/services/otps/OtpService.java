package io.studi.backend.services.otps;

import io.studi.backend.dtos.common.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface OtpService {
    ResponseEntity<ApiResponse<?>> sendOtp(String email);

    ResponseEntity<ApiResponse<?>> verifyOtp(String email, String otp);
}

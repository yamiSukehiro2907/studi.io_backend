package io.studi.backend.services.otps;

import com.cloudinary.Api;
import io.studi.backend.dtos.common.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface OtpService {
    ResponseEntity<ApiResponse<?>> emailVerification(String email);

    ResponseEntity<ApiResponse<?>> verifyOtp(String email , String otp);
}

package io.studi.backend.controllers;

import io.studi.backend.dtos.Requests.otps.SendEmailVerificationRequest;
import io.studi.backend.dtos.Requests.otps.VerifyOtpRequest;
import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.services.others.EmailService;
import io.studi.backend.services.otps.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {

    private final EmailService emailService;

    private final OtpService otpService;

    @PostMapping("/send-email-otp")
    public ResponseEntity<ApiResponse<?>> emailVerificationOtp(@Valid @RequestBody SendEmailVerificationRequest sendEmailVerificationRequest) {
        if (sendEmailVerificationRequest.email() != null) {
            return otpService.emailVerification(sendEmailVerificationRequest.email());
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("Invalid Email"));
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<?>> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        if (verifyOtpRequest.email() != null && verifyOtpRequest.otp() != null) {
            return otpService.verifyOtp(verifyOtpRequest.email(), verifyOtpRequest.otp());
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("Otp & Email are required!"));
    }
}

package io.studi.backend.services.otps;

import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.helpers.OtpHelper;
import io.studi.backend.models.Otp;
import io.studi.backend.models.User;
import io.studi.backend.repositories.otps.OtpRepository;
import io.studi.backend.repositories.user.UserRepository;
import io.studi.backend.services.others.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final UserRepository userRepository;

    private final OtpRepository otpRepository;

    private final EmailService emailService;


    @Override
    public ResponseEntity<ApiResponse<?>> sendOtp(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(ApiResponse.error("User not found!"), HttpStatus.NOT_FOUND);
        }
        otpRepository.deleteAll(email);
        String otp = OtpHelper.generateOtp();
        if (emailService.sendOtpMailVerification(email,otp)) {
            Otp otpObject = new Otp();
            otpObject.setOtp(otp);
            otpObject.setEmail(email);
            otpRepository.createOtp(otpObject);
            return ResponseEntity.ok(ApiResponse.success("Otp sent successfully"));
        }
        return new ResponseEntity<>(ApiResponse.error("Failed to send otp! Please try again"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(ApiResponse.error("User not found!"), HttpStatus.NOT_FOUND);
        }
        Otp otpObject = otpRepository.find(email,otp);
        if (otpObject == null) {
            return new ResponseEntity<>(ApiResponse.error("Invalid or Expired Otp!"), HttpStatus.BAD_REQUEST);
        }
        user.setVerified(true);
        otpRepository.deleteAll(email);
        userRepository.save(user);
        return ResponseEntity.ok().body(ApiResponse.success("Email verified successfully!"));
    }
}

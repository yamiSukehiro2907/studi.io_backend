package io.studi.backend.repositories.otps;

import io.studi.backend.models.Otp;

public interface OtpRepository {

    void createOtp(Otp otp);

    void deleteAll(String email);

    void save(Otp otp);

    Otp find(String email , String otp);
}

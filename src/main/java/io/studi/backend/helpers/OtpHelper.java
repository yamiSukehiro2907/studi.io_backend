package io.studi.backend.helpers;

import java.util.Random;

public class OtpHelper {

    public static String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}

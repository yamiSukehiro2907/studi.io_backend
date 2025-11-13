package io.studi.backend.helpers;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static boolean isEmail(String email) {

        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}

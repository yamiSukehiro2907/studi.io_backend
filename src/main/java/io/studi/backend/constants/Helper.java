package io.studi.backend.constants;

import io.studi.backend.dtos.Responses.UserDto;
import io.studi.backend.models.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.security.SecureRandom;
import java.util.Arrays;

public class Helper {

    private static final String CHAR_POOL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateUsername() {
        StringBuilder sb = new StringBuilder();
        int length = 7;
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    public static UserDto getUserDto(User createdUser) {
        return new UserDto(
                createdUser.getId(),
                createdUser.getName(),
                createdUser.getEmail(),
                createdUser.getUsername(),
                createdUser.getProfileImageUrl(),
                createdUser.getRefreshToken(),
                createdUser.getBio(),
                createdUser.isVerified(),
                createdUser.getCreatedAt(),
                createdUser.getUpdatedAt()
        );
    }

    public static String getAccessTokenFromHttpRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> "accessToken".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public static String getRefreshTokenFromHttpRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> "refreshToken".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public static String getJSESSIONID(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> "JSESSIONID".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}

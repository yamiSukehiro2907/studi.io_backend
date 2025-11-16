package io.studi.backend.helpers;

import io.studi.backend.dtos.users.UserDto;
import io.studi.backend.models.User;

import java.security.SecureRandom;

public class UserHelper {
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
                createdUser.getBio(),
                createdUser.isVerified(),
                createdUser.getCreatedAt(),
                createdUser.getUpdatedAt()
        );
    }
}

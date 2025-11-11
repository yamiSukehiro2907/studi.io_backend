package io.studi.backend.common.utils;

import io.studi.backend.auth.dtos.Responses.UserDto;
import io.studi.backend.auth.models.User;

public class Helper {

    public static String generateUsername() {
        return "username";
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
}

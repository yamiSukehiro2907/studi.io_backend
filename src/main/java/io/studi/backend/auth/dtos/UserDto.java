package io.studi.backend.auth.dtos;

import java.time.LocalDateTime;

public record UserDto(
        String id,
        String name,
        String email,
        String username,
        String profileImage,
        String refreshToken,
        String bio,
        boolean verified,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

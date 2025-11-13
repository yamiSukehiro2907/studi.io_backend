package io.studi.backend.dtos.others;

import java.time.LocalDateTime;

public record UserDto(
        String id,
        String name,
        String email,
        String username,
        String profileImage,
        String bio,
        boolean verified,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

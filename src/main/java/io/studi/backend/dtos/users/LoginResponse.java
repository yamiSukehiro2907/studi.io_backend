package io.studi.backend.dtos.users;

public record LoginResponse(
        String userId,
        String email,
        String username
) {
}

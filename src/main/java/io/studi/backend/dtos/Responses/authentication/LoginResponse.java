package io.studi.backend.dtos.Responses.authentication;

public record LoginResponse(
        String userId,
        String email,
        String username
) {
}

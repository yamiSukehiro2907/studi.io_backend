package io.studi.backend.dtos.Requests.user;

public record UpdateRequest(
        String name,
        String email,
        String username,
        String bio
) {
}

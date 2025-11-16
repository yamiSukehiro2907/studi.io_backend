package io.studi.backend.dtos.rooms;

public record Member(
        MemberDetail user,
        boolean isAdmin
) {
}
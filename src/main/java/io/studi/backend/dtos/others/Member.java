package io.studi.backend.dtos.others;

public record Member(
        MemberDetail user,
        boolean isAdmin
) {
}
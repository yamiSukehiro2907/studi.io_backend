package io.studi.backend.dtos.others;

import io.studi.backend.models.Section;

import java.time.LocalDateTime;
import java.util.List;

public record StudyRoomDto(
        String id,
        String name,
        String description,
        boolean isPrivate,
        MemberDetail owner,
        String roomImage,
        List<Member> members,
        String whiteboardState,
        List<Section> resourceHub,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

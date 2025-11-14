package io.studi.backend.dtos.Requests;

import jakarta.validation.constraints.NotNull;

public record StudyRoomRequest(
        @NotNull(message = "Name is required")
        String name,
        String description
) {
}

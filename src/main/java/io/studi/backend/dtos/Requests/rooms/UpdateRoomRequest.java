package io.studi.backend.dtos.Requests.rooms;

import jakarta.validation.constraints.NotNull;

public record UpdateRoomRequest(
        @NotNull(message = "Room name is required")
        String name,
        String description,
        Boolean isPrivate
) {
}

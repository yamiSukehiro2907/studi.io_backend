package io.studi.backend.dtos.messages;

import jakarta.validation.constraints.NotNull;

public record ChatMessage(
        @NotNull(message = "RoomId is required")
        String roomId,
        @NotNull(message = "Sender is required")
        String senderId,
        @NotNull(message = "Message cannot be empty!")
        String content
) {
}

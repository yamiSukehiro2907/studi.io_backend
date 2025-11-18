package io.studi.backend.dtos.messages;

public record Event(
        String userId,
        String type,
        Object content,
        String roomId
) {
}

package io.studi.backend.dtos.messages;

import jakarta.validation.constraints.NotNull;

public record MessageDto(
        @NotNull(message = "Message cannot be empty!")
        String content
) {
}

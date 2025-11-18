package io.studi.backend.dtos.whiteboard;

import jakarta.validation.constraints.NotNull;

public record WhiteBoardDto(
        @NotNull(message = "WhiteBoardState cannot be null")
        String whiteBoardState
) {
}

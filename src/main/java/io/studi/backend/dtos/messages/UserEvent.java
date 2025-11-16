package io.studi.backend.dtos.messages;

public record UserEvent (
        String type,
        String userId,
        String roomId
){
}

package io.studi.backend.dtos.Responses.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String id;
    private String content;
    private String room;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SenderInfo sender;
}

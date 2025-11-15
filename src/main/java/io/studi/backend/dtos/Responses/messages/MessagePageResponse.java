package io.studi.backend.dtos.Responses.messages;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MessagePageResponse {
    private List<MessageResponse> messages = List.of();
    private Integer totalMessages = 0;
}

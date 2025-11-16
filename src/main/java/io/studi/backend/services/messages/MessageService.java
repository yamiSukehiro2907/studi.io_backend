package io.studi.backend.services.messages;

import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.dtos.messages.ChatMessage;
import io.studi.backend.models.Message;
import org.springframework.http.ResponseEntity;

public interface MessageService {

    ResponseEntity<ApiResponse<?>> getMessages(String roomId, int page);

    Message createMessage(ChatMessage chatMessage);
}

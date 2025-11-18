package io.studi.backend.services.messages;

import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.models.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface MessageService {

    ResponseEntity<ApiResponse<?>> getMessages(String roomId, int page);

    Message createMessage(String roomId, String senderId, String content);

    ApiResponse<?> getWhiteBoardState(String roomId, String userId);

    ApiResponse<?> clearWhiteBoard(String roomId, String userId);

    void updateWhiteBoard(String roomId , String whiteBoardState);
}

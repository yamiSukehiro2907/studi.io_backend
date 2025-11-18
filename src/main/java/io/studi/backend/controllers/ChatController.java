package io.studi.backend.controllers;

import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.dtos.messages.Event;
import io.studi.backend.dtos.messages.MessageDto;
import io.studi.backend.dtos.messages.MessageResponse;
import io.studi.backend.dtos.messages.SenderInfo;
import io.studi.backend.dtos.whiteboard.WhiteBoardDto;
import io.studi.backend.helpers.MessageHelper;
import io.studi.backend.models.Message;
import io.studi.backend.services.messages.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;
    private final MessageHelper messageHelper;

    @MessageMapping("/sendMessage")
    /// frontend sends message at this
    public void sendMessage(@Payload MessageDto messageDto, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String roomId = messageHelper.getRoomId(sessionId);
        String userId = messageHelper.getUserId(sessionId);
        if (roomId == null || userId == null || messageDto.content() == null) return;
        Message message = messageService.createMessage(roomId, userId, messageDto.content());
        SenderInfo senderInfo = messageHelper.fillUserDetails(userId);
        MessageResponse messageResponse = new MessageResponse(message.getId().toHexString(), message.getContent(), roomId, message.getCreatedAt(), message.getUpdatedAt(), senderInfo);
        Event event = new Event(userId, "new-message", messageResponse, roomId);
        simpMessagingTemplate.convertAndSend("/client/room/" + roomId, event);
    }

    @MessageMapping("/getWhiteboardState")
    public void getWhiteboardState(SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String roomId = messageHelper.getRoomId(sessionId);
        String userId = messageHelper.getUserId(sessionId);
        if (roomId == null || userId == null) return;
        ApiResponse<?> whiteBoardState = messageService.getWhiteBoardState(roomId, userId);
        if (whiteBoardState.success()) {
            Event event = new Event(userId, "whiteboard", whiteBoardState.data(), roomId);
            simpMessagingTemplate.convertAndSendToUser(userId, "/queue/reply", event);
        }
    }

    @MessageMapping("/clearWhiteboard")
    public void clearWhiteBoard(SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String roomId = messageHelper.getRoomId(sessionId);
        String userId = messageHelper.getUserId(sessionId);
        if (roomId == null || userId == null) return;
        ApiResponse<?> response = messageService.clearWhiteBoard(roomId, userId);
        if (response.success()) {
            Event event = new Event(userId, "clear-whiteboard", null, roomId);
            simpMessagingTemplate.convertAndSend("/client/room/" + roomId, event);
        }
    }

    @MessageMapping("/drawing")
    public void drawing(@Payload WhiteBoardDto whiteBoardDto, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String roomId = messageHelper.getRoomId(sessionId);
        String userId = messageHelper.getUserId(sessionId);
        if (roomId == null || userId == null) return;
        Event event = new Event(userId, "drawing", whiteBoardDto.whiteBoardState(), roomId);
        simpMessagingTemplate.convertAndSend("/client/room/" + roomId, event);
        messageService.updateWhiteBoard(roomId, whiteBoardDto.whiteBoardState());
    }
}

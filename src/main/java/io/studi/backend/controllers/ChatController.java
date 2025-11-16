package io.studi.backend.controllers;

import io.studi.backend.dtos.messages.ChatMessage;
import io.studi.backend.models.Message;
import io.studi.backend.services.messages.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/sendMessage") /// frontend sends message at this
    public void sendMessage(ChatMessage chatMessage) {
        Message message = messageService.createMessage(chatMessage);
        if (message == null) {
            log.warn("Error creating and sending message: ");
            return;
        }
        String destination = "/server/room" + chatMessage.roomId();
        simpMessagingTemplate.convertAndSend(destination, message);
        ///  server sends the message object created to this endpoint
    }

}

package io.studi.backend.services.messages;

import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.dtos.messages.ChatMessage;
import io.studi.backend.dtos.messages.MessagePageResponse;
import io.studi.backend.models.Message;
import io.studi.backend.models.StudyRoom;
import io.studi.backend.repositories.message.MessageRepository;
import io.studi.backend.repositories.studyRoom.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final StudyRoomRepository studyRoomRepository;

    private static final int MESSAGES_PER_PAGE = 50;

    @Override
    public ResponseEntity<ApiResponse<?>> getMessages(String roomId, int page) {
        if (!ObjectId.isValid(roomId)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid Room ID!"));
        }
        ObjectId roomObjectId = new ObjectId(roomId);
        StudyRoom studyRoom = studyRoomRepository.findById(roomObjectId);
        if (studyRoom == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Room Not Found!"));
        }
        MessagePageResponse response = messageRepository.getMessages(roomObjectId, page, MESSAGES_PER_PAGE);
        int totalMessages = response.getTotalMessages() != null ? response.getTotalMessages() : 0;
        int totalPages = (int) Math.ceil((double) totalMessages / MESSAGES_PER_PAGE);
        return ResponseEntity.ok(
                ApiResponse.success("Messages fetched successfully!", Map.of(
                                "messages", response.getMessages(),
                                "pagination", Map.of(
                                        "currentPage", page,
                                        "totalPages", totalPages,
                                        "totalMessages", totalMessages,
                                        "hasNextPage", page < totalPages,
                                        "hasPrevPage", page > 1
                                )
                        )
                )
        );
    }

    @Override
    public Message createMessage(ChatMessage chatMessage) {
        if (!ObjectId.isValid(chatMessage.roomId()) || !ObjectId.isValid(chatMessage.senderId())) {
            return null;
        }
        Message message = new Message();
        message.setRoom(new ObjectId(chatMessage.roomId()));
        message.setSender(new ObjectId(chatMessage.senderId()));
        message.setContent(chatMessage.content());
        messageRepository.save(message);
        return message;
    }
}

package io.studi.backend.services.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.dtos.messages.MessagePageResponse;
import io.studi.backend.dtos.whiteboard.WhiteboardCanvasState;
import io.studi.backend.models.Message;
import io.studi.backend.models.StudyRoom;
import io.studi.backend.repositories.message.MessageRepository;
import io.studi.backend.repositories.studyRoom.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
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
        return ResponseEntity.ok(ApiResponse.success("Messages fetched successfully!", Map.of("messages", response.getMessages(), "pagination", Map.of("currentPage", page, "totalPages", totalPages, "totalMessages", totalMessages, "hasNextPage", page < totalPages, "hasPrevPage", page > 1))));
    }

    @Override
    public Message createMessage(String roomId, String senderId, String content) {
        if (!ObjectId.isValid(roomId) || !ObjectId.isValid(senderId)) {
            return null;
        }
        Message message = new Message();
        message.setRoom(new ObjectId(roomId));
        message.setSender(new ObjectId(senderId));
        message.setContent(content);
        messageRepository.save(message);
        return message;
    }

    @Override
    public ApiResponse<?> getWhiteBoardState(String roomId, String userId) {
        if (!ObjectId.isValid(roomId) || !ObjectId.isValid(userId)) return null;
        StudyRoom studyRoom = studyRoomRepository.findById(new ObjectId(roomId));
        if (studyRoom == null) return ApiResponse.error("Room Not Found");
        boolean isPresent = studyRoom.getMembers().stream().anyMatch(user -> user.getUserId().equals(new ObjectId(userId)));
        if (!isPresent) return ApiResponse.error("You are not a member");
        return ApiResponse.success("Fetched Successfully!", studyRoom.getWhiteboardState());
    }

    @Override
    public ApiResponse<?> clearWhiteBoard(String roomId, String userId) {
        if (!ObjectId.isValid(roomId) || !ObjectId.isValid(userId)) return ApiResponse.error("Invalid RoomId");
        StudyRoom studyRoom = studyRoomRepository.findById(new ObjectId(roomId));
        if (studyRoom == null) return ApiResponse.error("Room Not Found");
        boolean isOwner = studyRoom.getOwnerId().equals(new ObjectId(userId));
        boolean isAdmin = studyRoom.getMembers().stream().anyMatch(user -> user.getIsAdmin() && user.getUserId().equals(new ObjectId(userId)));
        if (isAdmin || isOwner) {
            studyRoom.setWhiteboardState("");
            studyRoomRepository.save(studyRoom);
            return ApiResponse.success("Cleared successfully!");
        }
        return ApiResponse.error("Only admins and owners are allowed.");
    }

    @Override
    public void updateWhiteBoard(String roomId, String whiteBoardStateJson) {
        StudyRoom room = studyRoomRepository.findById(new ObjectId(roomId));
        if (room == null) return;
        ObjectMapper mapper = new ObjectMapper();
        WhiteboardCanvasState canvasState;
        try {
            if (room.getWhiteboardState() != null && !room.getWhiteboardState().isBlank()) {
                canvasState = mapper.readValue(room.getWhiteboardState(), WhiteboardCanvasState.class);
            } else {
                canvasState = new WhiteboardCanvasState("6.7.1", new ArrayList<>());
            }
        } catch (Exception e) {
            canvasState = new WhiteboardCanvasState("6.7.1", new ArrayList<>());
        }
        if (canvasState.getObjects() == null) {
            canvasState.setObjects(new ArrayList<>());
        }
        try {
            Object newObject = mapper.readValue(whiteBoardStateJson, Object.class);
            canvasState.getObjects().add(newObject);
        } catch (Exception e) {
            log.warn("Error loading whiteboardState");
            return;
        }

        try {
            String updatedJson = mapper.writeValueAsString(canvasState);
            room.setWhiteboardState(updatedJson);
            studyRoomRepository.save(room);
        } catch (Exception e) {
            log.warn("Error updating whiteBoardState");
        }
    }

}

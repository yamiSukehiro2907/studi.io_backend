package io.studi.backend.services.studyRoom;

import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.models.User;
import org.springframework.http.ResponseEntity;

public interface StudyRoomService {

    ResponseEntity<ApiResponse<?>> createStudyRoom(String name , String description);

    ResponseEntity<ApiResponse<?>> getRooms(User user);

    ResponseEntity<ApiResponse<?>> getRoom(String roomId);
}

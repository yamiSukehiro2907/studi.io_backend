package io.studi.backend.services.studyRoom;

import io.studi.backend.dtos.Requests.rooms.UpdateRoomRequest;
import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface StudyRoomService {

    ResponseEntity<ApiResponse<?>> createStudyRoom(String name, String description);

    ResponseEntity<ApiResponse<?>> getRooms(User user);

    ResponseEntity<ApiResponse<?>> getRoom(String roomId);

    ResponseEntity<ApiResponse<?>> joinPublicRoom(String roomId, User user);

    ResponseEntity<ApiResponse<?>> updateRoomInfo(String roomId, UpdateRoomRequest updateRoomRequest, MultipartFile file);
}

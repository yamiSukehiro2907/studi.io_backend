package io.studi.backend.services.studyRoom;

import io.studi.backend.dtos.common.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface StudyRoomService {

    ResponseEntity<ApiResponse<?>> createStudyRoom(String name , String description);
}

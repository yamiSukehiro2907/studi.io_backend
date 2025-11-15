package io.studi.backend.services.studyRoom;

import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.dtos.others.StudyRoomDto;
import io.studi.backend.helpers.StudyRoomHelper;
import io.studi.backend.models.StudyRoom;
import io.studi.backend.models.User;
import io.studi.backend.repositories.studyRoom.StudyRoomRepository;
import io.studi.backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyRoomServiceImpl implements StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;

    private final StudyRoomHelper studyRoomHelper;

    @Override
    public ResponseEntity<ApiResponse<?>> createStudyRoom(String name, String description) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        StudyRoom studyRoom = studyRoomHelper.createStudyRoom(name, description, customUserDetails);
        studyRoomRepository.save(studyRoom);
        StudyRoomDto studyRoomDto = studyRoomHelper.populate(studyRoom);
        return new ResponseEntity<>(
                ApiResponse.success("", studyRoomDto),
                HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getRooms(User user) {
        List<StudyRoom> studyRooms = studyRoomRepository.findAll(user.getId());
        List<StudyRoomDto> studyRoomDtoList = studyRooms.stream().map(studyRoomHelper::populate).toList();
        return ResponseEntity.ok().body(ApiResponse.success("Rooms fetched successfully!", studyRoomDtoList));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getRoom(String roomId) {
        StudyRoom studyRoom = studyRoomRepository.findById(new ObjectId(roomId));
        return ResponseEntity.ok().body(ApiResponse.success("Room fetched successfully!", studyRoomHelper.populate(studyRoom)));
    }

}

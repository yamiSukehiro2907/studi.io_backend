package io.studi.backend.controllers;

import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.dtos.Requests.StudyRoomRequest;
import io.studi.backend.security.CustomUserDetails;
import io.studi.backend.services.studyRoom.StudyRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createStudyRoom(@Valid @RequestBody StudyRoomRequest studyRoomRequest) {
        if (studyRoomRequest.name() != null) {
            return studyRoomService.createStudyRoom(studyRoomRequest.name(), studyRoomRequest.description());
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("Name is required"));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> getAllRooms(){
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return studyRoomService.getRooms(userDetails.getUser());
    }
}

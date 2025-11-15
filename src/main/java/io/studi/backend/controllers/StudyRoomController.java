package io.studi.backend.controllers;

import io.studi.backend.dtos.Requests.rooms.StudyRoomRequest;
import io.studi.backend.dtos.Requests.rooms.UpdateRoomRequest;
import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.security.CustomUserDetails;
import io.studi.backend.services.studyRoom.StudyRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<ApiResponse<?>> getAllRooms() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return studyRoomService.getRooms(userDetails.getUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getRoom(@PathVariable(name = "id") String roomId) {
        if (roomId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("RoomId is required!"));
        }
        return studyRoomService.getRoom(roomId);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<?>> joinPublicRoom(@PathVariable(name = "id") String roomId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (roomId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("RoomId is required!"));
        }
        return studyRoomService.joinPublicRoom(roomId, customUserDetails.getUser());
    }

    @PutMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> updateRoomInfo(@PathVariable(name = "id") String roomId, @ModelAttribute UpdateRoomRequest updateRoomRequest, @RequestParam(value = "file", required = false) MultipartFile file) {
        if (roomId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("RoomId is required!"));
        }
        return studyRoomService.updateRoomInfo(roomId , updateRoomRequest , file);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteRoom(@PathVariable(name = "id") String roomId){
        if (roomId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("RoomId is required!"));
        }
        return studyRoomService.deleteRoom(roomId);
    }
}


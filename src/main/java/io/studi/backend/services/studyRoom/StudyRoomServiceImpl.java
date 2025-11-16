package io.studi.backend.services.studyRoom;

import io.studi.backend.dtos.Requests.rooms.UpdateRoomRequest;
import io.studi.backend.dtos.common.ApiResponse;
import io.studi.backend.dtos.rooms.Member;
import io.studi.backend.dtos.rooms.StudyRoomDto;
import io.studi.backend.helpers.StudyRoomHelper;
import io.studi.backend.models.StudyRoom;
import io.studi.backend.models.StudyRoomMember;
import io.studi.backend.models.User;
import io.studi.backend.repositories.studyRoom.StudyRoomRepository;
import io.studi.backend.security.CustomUserDetails;
import io.studi.backend.services.others.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudyRoomServiceImpl implements StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;

    private final StudyRoomHelper studyRoomHelper;

    private final CloudinaryService cloudinaryService;

    @Override
    public ResponseEntity<ApiResponse<?>> createStudyRoom(String name, String description) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        StudyRoom studyRoom = studyRoomHelper.createStudyRoom(name, description, customUserDetails);
        studyRoomRepository.save(studyRoom);
        StudyRoomDto studyRoomDto = studyRoomHelper.populate(studyRoom);
        return new ResponseEntity<>(ApiResponse.success("", studyRoomDto), HttpStatus.CREATED);
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
        if (studyRoom == null) {
            return new ResponseEntity<>(ApiResponse.error("Room Not Found!"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(ApiResponse.success("Room fetched successfully!", studyRoomHelper.populate(studyRoom)));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> joinPublicRoom(String roomId, User user) {
        String userId = user.getId();
        StudyRoom studyRoom = studyRoomRepository.findById(new ObjectId(roomId));
        if (studyRoom == null) {
            return new ResponseEntity<>(ApiResponse.error("Room Not Found!"), HttpStatus.NOT_FOUND);
        }
        if (studyRoom.getIsPrivate()) {
            return new ResponseEntity<>(ApiResponse.error("This room is private and requires an invitation."), HttpStatus.FORBIDDEN);
        }
        StudyRoomDto studyRoomDto = studyRoomHelper.populate(studyRoom);
        List<Member> members = studyRoomDto.members();
        for (Member member : members) {
            if (member.user().id().equals(userId)) {
                return ResponseEntity.ok().body(ApiResponse.success("Already a member!", studyRoomDto));
            }
        }
        StudyRoomMember studyRoomMember = new StudyRoomMember(new ObjectId(userId), false);
        studyRoom.getMembers().add(studyRoomMember);
        studyRoomRepository.save(studyRoom);
        StudyRoom updatedStudyRoom = studyRoomRepository.findById(new ObjectId(roomId));
        return ResponseEntity.ok().body(ApiResponse.success("Successfully joined the room!", studyRoomHelper.populate(updatedStudyRoom)));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateRoomInfo(String roomId, UpdateRoomRequest updateRoomRequest, MultipartFile file) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = customUserDetails.getUser();
        StudyRoom studyRoom = studyRoomRepository.findById(new ObjectId(roomId));
        if (studyRoom == null) {
            return new ResponseEntity<>(ApiResponse.error("Room Not Found!"), HttpStatus.NOT_FOUND);
        }
        boolean isOwner = studyRoom.getOwnerId().equals(new ObjectId(user.getId()));
        boolean isAdmin = studyRoom.getMembers().stream().anyMatch(u -> u.getIsAdmin() && u.getUserId().equals(new ObjectId(user.getId())));
        if (isOwner || isAdmin) {
            if (file != null && !file.isEmpty()) {
                ApiResponse<Map<String, String>> response = cloudinaryService.uploadFile(file);
                if (response.success()) studyRoom.setRoomImage(response.data().get("secure_url"));
                else
                    return new ResponseEntity<>(ApiResponse.error("Error uploading room Image."), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (updateRoomRequest.isPrivate() != null) {
                studyRoom.setIsPrivate(updateRoomRequest.isPrivate());
            }
            if (updateRoomRequest.name() != null) {
                studyRoom.setName(updateRoomRequest.name().trim());
            }
            if (updateRoomRequest.description() != null) {
                studyRoom.setDescription(updateRoomRequest.description().trim());
            }
            studyRoomRepository.save(studyRoom);
            return ResponseEntity.ok().body(ApiResponse.success("Successfully updated the room Info!", studyRoomHelper.populate(studyRoom)));
        }
        return new ResponseEntity<>(ApiResponse.error("Only Admin and Owner are allowed.."), HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> deleteRoom(String roomId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = customUserDetails.getUser();
        StudyRoom studyRoom = studyRoomRepository.findById(new ObjectId(roomId));
        if (studyRoom == null) {
            return new ResponseEntity<>(ApiResponse.error("Room Not Found!"), HttpStatus.NOT_FOUND);
        }
        boolean isOwner = studyRoom.getOwnerId().equals(new ObjectId(user.getId()));
        if (isOwner) {
            studyRoomRepository.deleteRoom(new ObjectId(roomId));
            return ResponseEntity.ok().body(ApiResponse.success("Room deleted successfully!"));
        }
        return new ResponseEntity<>(ApiResponse.error("Only owners are allowed to delete the room"), HttpStatus.FORBIDDEN);
    }

}

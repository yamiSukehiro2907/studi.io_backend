package io.studi.backend.helpers;

import io.studi.backend.dtos.rooms.Member;
import io.studi.backend.dtos.rooms.MemberDetail;
import io.studi.backend.dtos.rooms.StudyRoomDto;
import io.studi.backend.models.StudyRoom;
import io.studi.backend.models.StudyRoomMember;
import io.studi.backend.models.User;
import io.studi.backend.repositories.user.UserRepository;
import io.studi.backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StudyRoomHelper {

    private final UserRepository userRepository;

    public StudyRoomDto populate(StudyRoom studyRoom) {
        User ownerUser = userRepository.findById(studyRoom.getOwnerId());
        if (ownerUser == null) {
            throw new RuntimeException("Owner not found!");
        }
        MemberDetail owner = new MemberDetail(
                        ownerUser.getId(),
                        ownerUser.getName(),
                        ownerUser.getEmail(),
                        ownerUser.getProfileImageUrl()
        );
        List<ObjectId> memberIds = studyRoom.getMembers().stream().map(StudyRoomMember::getUserId).toList();
        List<User> memberUsers = userRepository.findAllById(memberIds);
        Map<String, User> userMap = memberUsers.stream().collect(Collectors.toMap(User::getId, user -> user));
        List<Member> members = studyRoom.getMembers().stream().map(m -> {
                    User u = userMap.get(m.getUserId().toHexString());
                    if (u == null) return null;
                    return new Member(
                            new MemberDetail(
                                    u.getId(),
                                    u.getName(),
                                    u.getEmail(),
                                    u.getProfileImageUrl()
                            ),
                            m.getIsAdmin()
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new StudyRoomDto(
                studyRoom.getId().toHexString(),
                studyRoom.getName(),
                studyRoom.getDescription(),
                studyRoom.getIsPrivate(),
                owner,
                studyRoom.getRoomImage(),
                members,
                studyRoom.getWhiteboardState(),
                studyRoom.getResourceHub(),
                studyRoom.getCreatedAt(),
                studyRoom.getUpdatedAt()
        );
    }

    @NotNull
    public StudyRoom createStudyRoom(String name, String description, CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        ObjectId userId = new ObjectId(user.getId());
        StudyRoom studyRoom = new StudyRoom();
        studyRoom.setName(name.trim());
        studyRoom.setOwnerId(userId);
        if (description != null && !description.isEmpty()) {
            studyRoom.setDescription(description.trim());
        }
        StudyRoomMember studyRoomMember = new StudyRoomMember(userId, true);
        studyRoom.setMembers(List.of(studyRoomMember));
        return studyRoom;
    }
}

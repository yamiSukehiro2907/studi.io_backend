package io.studi.backend.helpers;

import io.studi.backend.dtos.others.Member;
import io.studi.backend.dtos.others.MemberDetail;
import io.studi.backend.dtos.others.StudyRoomDto;
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

    public StudyRoomDto populate(StudyRoom studyRoom) throws RuntimeException {
        User ownerUser = userRepository.findById(studyRoom.getOwnerId());
        if (ownerUser == null) {
            throw new RuntimeException("Owner not found!");
        }
        Member owner = new Member(
                new MemberDetail(
                        ownerUser.getId(),
                        ownerUser.getName(),
                        ownerUser.getEmail(),
                        ownerUser.getProfileImageUrl()
                ),
                true
        );
        List<ObjectId> memberIds = studyRoom.getMembers().stream().map(StudyRoomMember::getUserId).toList();
        List<User> membersUsers = userRepository.findAllById(memberIds);
        Map<String, User> userMap = membersUsers.stream().collect(Collectors.toMap(User::getId, u -> u));
        List<Member> members = studyRoom.getMembers().stream()
                .map(member -> {
                    String id = member.getUserId().toHexString();
                    if (id.equals(ownerUser.getId())) return null;
                    User user = userMap.get(id);
                    if (user == null) return null;
                    MemberDetail detail = new MemberDetail(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getProfileImageUrl()
                    );
                    return new Member(detail, member.getIsAdmin());
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
            studyRoom.setDescription(description);
        }
        StudyRoomMember studyRoomMember = new StudyRoomMember();
        studyRoomMember.setUserId(userId);
        studyRoomMember.setIsAdmin(true);
        studyRoom.setMembers(List.of(studyRoomMember));
        return studyRoom;
    }
}

package io.studi.backend.helpers;

import io.studi.backend.config.websocket.RoomSessionRegistry;
import io.studi.backend.dtos.messages.SenderInfo;
import io.studi.backend.models.User;
import io.studi.backend.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageHelper {

    private final RoomSessionRegistry registry;
    private final UserRepository userRepository;

    public String getRoomId(String sessionId) {
        return registry.getSessionRoomMap().get(sessionId);
    }

    public String getUserId(String sessionId) {
        return registry.getSessionUserMap().get(sessionId);
    }

    public SenderInfo fillUserDetails(String userId) {
        if (!ObjectId.isValid(userId)) {
            return null;
        }
        User user = userRepository.loadUserById(userId);
        SenderInfo senderInfo = new SenderInfo();
        senderInfo.setId(userId);
        senderInfo.setName(user.getName());
        senderInfo.setProfileImage(user.getProfileImageUrl());
        return senderInfo;
    }
}

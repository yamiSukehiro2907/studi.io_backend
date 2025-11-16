package io.studi.backend.config.websocket;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
public class RoomSessionRegistry {

    private final Map<String, String> sessionRoomMap = new ConcurrentHashMap<>();
    /// mapping for sessionId to RoomId

    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
    /// mapping for sessionId to userId

    private final Map<String, Set<String>> roomMembersMap = new ConcurrentHashMap<>();
    /// mapping for roomId to userId present in that room

    public void joinRoom(String sessionId , String roomId , String userId){
        sessionRoomMap.put(sessionId , roomId);
        /// userId mapped to sessionId
        sessionUserMap.put(sessionId , userId);

        roomMembersMap.putIfAbsent(roomId , ConcurrentHashMap.newKeySet());
        roomMembersMap.get(roomId).add(userId);
    }

    public void leaveRoom(String sessionId){
        String roomId = sessionRoomMap.get(sessionId);
        String userId = sessionUserMap.get(sessionId);

        if(roomId == null || userId == null) return;

        roomMembersMap.getOrDefault(roomId , ConcurrentHashMap.newKeySet()).remove(userId);

        sessionRoomMap.remove(sessionId);
        sessionUserMap.remove(sessionId);
    }
}

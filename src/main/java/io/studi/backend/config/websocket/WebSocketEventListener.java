package io.studi.backend.config.websocket;

import io.studi.backend.dtos.messages.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final RoomSessionRegistry registry;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();
        if (accessor.getSessionAttributes() != null || !accessor.getSessionAttributes().containsKey("userId")) return;
        String userId = (String) accessor.getSessionAttributes().get("userId");
        if (destination == null || !destination.startsWith("/server/room")) return;
        String roomId = destination.replace("/server/room/", "");
        registry.joinRoom(sessionId, roomId, userId);
        /// sending user-joined to the room
        messagingTemplate.convertAndSend(
                "/server/room/" + roomId,
                new UserEvent("user-joined", userId, roomId)
        );
    }

    @EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        leaveRoomAndNotify(accessor.getSessionId());
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        leaveRoomAndNotify(event.getSessionId());
    }

    private void leaveRoomAndNotify(String sessionId){
        String roomId = registry.getSessionRoomMap().get(sessionId);
        String userId = registry.getSessionUserMap().get(sessionId);
        if (roomId == null) return;
        registry.leaveRoom(sessionId);
        messagingTemplate.convertAndSend(
                "/server/room/" + roomId,
                new UserEvent("user-left", userId, roomId)
        );
    }
}

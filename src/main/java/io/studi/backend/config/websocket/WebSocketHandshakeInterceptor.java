package io.studi.backend.config.websocket;

import io.studi.backend.helpers.AuthHelper;
import io.studi.backend.models.User;
import io.studi.backend.repositories.user.UserRepository;
import io.studi.backend.security.CustomUserDetails;
import io.studi.backend.utils.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;


    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        String accessToken = AuthHelper.getAccessTokenFromHttpRequest(req);
        if (accessToken != null) {
            String userId = jwtUtil.getIdAccessToken(accessToken);
            User user = userRepository.loadUserById(userId);
            if (jwtUtil.isValidAccessToken(accessToken, new CustomUserDetails(user))) {
                attributes.put("userId", userId);
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, Exception exception) {

    }
}

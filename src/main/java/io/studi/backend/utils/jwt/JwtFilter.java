package io.studi.backend.utils.jwt;

import io.studi.backend.helpers.AuthHelper;
import io.studi.backend.security.CustomUserDetails;
import io.studi.backend.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/ws")) {
            return true;
        }
        return path.startsWith("/auth/");
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = AuthHelper.getAccessTokenFromHttpRequest(request);
        if (accessToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String userId = jwtUtil.getIdAccessToken(accessToken);
                CustomUserDetails userDetails = userDetailsService.loadUserById(userId);

                if (jwtUtil.isValidAccessToken(accessToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("JWT validated successfully for userId: {}", userId);
                }

            } catch (Exception e) {
                log.error("JWT validation failed for request [{}]: {}", request.getRequestURI(), e.getMessage());
            }

        } else if (accessToken == null) {

            log.trace("No JWT token found in request [{}]", request.getRequestURI());

            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"success\":false,\"message\":\"Token not provided\"}"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}

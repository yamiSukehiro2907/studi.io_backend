package io.studi.backend.config;

import io.github.bucket4j.Bucket;
import io.studi.backend.security.CustomUserDetails;
import io.studi.backend.services.others.RateLimiterService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class RateLimitFilter implements Filter {

    private final RateLimiterService rateLimiterService;

    @PostConstruct
    public void testEnvLoad() {
        System.out.println("ACCESS FROM SPRING = " + System.getenv("ACCESS_TOKEN_SECRET"));
        System.out.println("ACCESS FROM SPRING = " + System.getProperty("ACCESS_TOKEN_SECRET"));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();

        Bucket generalBucket = rateLimiterService.resolveGeneralBucket(ip);

        if (!generalBucket.tryConsume(1)) {
            tooMany(response, "Too many requests globally!");
            return;
        }

        if (path.equals("/auth/login")) {
            Bucket loginBucket = rateLimiterService.resolveLoginBucket(ip);
            if (!loginBucket.tryConsume(1)) {
                tooMany(response, "Too many login attempts!");
                return;
            }
        }

        if (path.equals("/auth/signup")) {
            Bucket signupBucket = rateLimiterService.resolveSignupBucket(ip);
            if (!signupBucket.tryConsume(1)) {
                tooMany(response, "Too many signup attempts!");
                return;
            }
        }

        if (!path.startsWith("/auth/")) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                var principal = auth.getPrincipal();

                if (principal instanceof CustomUserDetails customUserDetails) {
                    String userId = customUserDetails.getUser().getId();

                    Bucket userBucket = rateLimiterService.resolveUserBucket(userId);

                    if (!userBucket.tryConsume(1)) {
                        tooMany(response, "Too many requests for user: " + userId);
                        return;
                    }
                }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void tooMany(HttpServletResponse response, String message) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"success\":false,\"message\":\"" + message + "\"}"
        );
    }
}

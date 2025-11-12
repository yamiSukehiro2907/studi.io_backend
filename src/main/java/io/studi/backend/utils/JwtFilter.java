package io.studi.backend.utils;

import io.studi.backend.security.CustomUserDetails;
import io.studi.backend.security.CustomUserDetailsService;
import io.studi.backend.constants.Helper;
import io.studi.backend.constants.LoggerHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtUtil _jwtUtil, CustomUserDetailsService _userDetailsService) {
        this.jwtUtil = _jwtUtil;
        this.userDetailsService = _userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = Helper.getAccessTokenFromHttpRequest(request);

        if (accessToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String userId = jwtUtil.getIdAccessToken(accessToken);
                CustomUserDetails userDetails = userDetailsService.loadUserById(userId);

                if (jwtUtil.validateAccessToken(accessToken, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                LoggerHelper.error(this, "JWT validation failed: " + e.getMessage(), e);
            }
        }

        filterChain.doFilter(request, response);
    }
}

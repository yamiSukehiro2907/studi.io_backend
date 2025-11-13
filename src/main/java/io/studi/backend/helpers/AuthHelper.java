package io.studi.backend.helpers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

public class AuthHelper {

    public static Cookie createAccessTokenCookie(String accessToken, int maxAge) {
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(maxAge);
        return accessCookie;
    }

    public static Cookie createRefreshTokenCookie(String refreshToken, int maxAge) {
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(maxAge);
        return refreshCookie;
    }


    public static String getAccessTokenFromHttpRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies()).filter(c -> "accessToken".equals(c.getName())).map(Cookie::getValue).findFirst().orElse(null);
        }
        return null;
    }

    public static String getRefreshTokenFromHttpRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies()).filter(c -> "refreshToken".equals(c.getName())).map(Cookie::getValue).findFirst().orElse(null);
        }
        return null;
    }
}

package io.studi.backend.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.studi.backend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${auth.ACCESS_TOKEN_SECRET}")
    private String accessTokenSecret;

    @Value("${auth.REFRESH_TOKEN_SECRET}")
    private String refreshTokenSecret;

    @Value("${auth.ACCESS_TOKEN_EXPIRATION}")
    private Long accessTokenExpiration;

    @Value("${auth.REFRESH_TOKEN_EXPIRATION}")
    private Long refreshTokenExpiration;

    private Key getAccessTokenSigningKey() {
        return Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    private Key getRefreshTokenSigningKey() {
        return Keys.hmacShaKeyFor(refreshTokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        claims.put("email", userDetails.getEmail());
        claims.put("username", userDetails.getUsername());
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(userDetails.getId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getAccessTokenSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(userDetails.getId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getRefreshTokenSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaimsInAccessToken(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getAccessTokenSigningKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }

    private Claims extractAllClaimsInRefreshToken(String refreshToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getRefreshTokenSigningKey())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
    }

    private <T> T extractClaimAccessToken(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaimsInAccessToken(token);
        return resolver.apply(claims);
    }

    private <T> T extractClaimRefreshToken(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaimsInRefreshToken(token);
        return resolver.apply(claims);
    }

    public String getUsername(String accessToken) {
        return extractClaimAccessToken(accessToken, claims -> claims.get("username", String.class));
    }

    public String getEmail(String accessToken) {
        return extractClaimAccessToken(accessToken, claims -> claims.get("email", String.class));
    }

    public String getIdAccessToken(String accessToken) {
        return extractClaimAccessToken(accessToken, Claims::getSubject);
    }

    public String getIdRefreshToken(String refreshToken) {
        return extractClaimRefreshToken(refreshToken, Claims::getSubject);
    }

    private boolean isAccessTokenExpired(String token) {
        Date exp = extractClaimAccessToken(token, Claims::getExpiration);
        return exp.before(new Date());
    }

    private boolean isRefreshTokenExpired(String token) {
        Date exp = extractClaimRefreshToken(token, Claims::getExpiration);
        return exp.before(new Date());
    }

    private String getAccessTokenType(String accessToken) {
        return extractClaimAccessToken(accessToken, claims -> claims.get("type", String.class));
    }

    private String getRefreshTokenType(String refreshToken) {
        return extractClaimRefreshToken(refreshToken, claims -> claims.get("type", String.class));
    }

    public boolean isValidAccessToken(String token, CustomUserDetails userDetails) {
        final String username = getUsername(token);
        final String email = getEmail(token);
        return username.equals(userDetails.getUsername()) &&
                email.equals(userDetails.getEmail()) &&
                getAccessTokenType(token).equals("access") &&
                !isAccessTokenExpired(token);
    }

    public boolean isValidRefreshToken(String token) {
        return !isRefreshTokenExpired(token) &&
                getRefreshTokenType(token).equals("refresh");
    }
}

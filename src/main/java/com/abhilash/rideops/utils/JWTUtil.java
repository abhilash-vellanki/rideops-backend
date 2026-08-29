package com.abhilash.rideops.utils;

import com.abhilash.rideops.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class JWTUtil {
    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_ISSUER = "rideops-backend";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";
    private static final Duration ACCESS_TOKEN_LIFETIME = Duration.ofMinutes(10);
    private static final Duration REFRESH_TOKEN_LIFETIME = Duration.ofDays(30);

    private final SecretKey secretKey;

    public JWTUtil(@Value("${jwt.secretKey}") String jwtSecretKey) {
        if (jwtSecretKey == null || jwtSecretKey.isBlank()) {
            throw new IllegalArgumentException("JWT secret key must be configured");
        }
        this.secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(TOKEN_ISSUER)
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ACCESS_TOKEN_LIFETIME)))
                .signWith(secretKey)
                .compact();
    }

    public GeneratedRefreshToken generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(REFRESH_TOKEN_LIFETIME);
        String tokenId = UUID.randomUUID().toString();
        String token = Jwts.builder()
                .subject(user.getId().toString())
                .issuer(TOKEN_ISSUER)
                .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE)
                .id(tokenId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
        return new GeneratedRefreshToken(token, tokenId, expiresAt);
    }

    public TokenClaims parseAccessToken(String token) {
        return parseToken(token, ACCESS_TOKEN_TYPE);
    }

    public TokenClaims parseRefreshToken(String token) {
        return parseToken(token, REFRESH_TOKEN_TYPE);
    }

    private TokenClaims parseToken(String token, String expectedType) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(TOKEN_ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);
        if (!expectedType.equals(tokenType)) {
            throw new JwtException("Unexpected JWT type");
        }
        if (claims.getId() == null || claims.getId().isBlank()) {
            throw new JwtException("JWT identifier is missing");
        }
        return new TokenClaims(
                Long.valueOf(claims.getSubject()),
                claims.getId(),
                claims.getExpiration().toInstant()
        );
    }

    public record GeneratedRefreshToken(String token, String tokenId, Instant expiresAt) {
    }

    public record TokenClaims(Long userId, String tokenId, Instant expiresAt) {
    }
}

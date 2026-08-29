package com.abhilash.rideops.utils;

import com.abhilash.rideops.entities.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JWTUtilTest {
    private final JWTUtil jwtUtil = new JWTUtil("a-secure-test-key-that-is-at-least-32-bytes-long");

    @Test
    void keepsAccessAndRefreshTokensDistinct() {
        User user = new User();
        user.setId(42L);

        String accessToken = jwtUtil.generateAccessToken(user);
        JWTUtil.GeneratedRefreshToken refreshToken = jwtUtil.generateRefreshToken(user);

        assertEquals(42L, jwtUtil.parseAccessToken(accessToken).userId());
        assertEquals(42L, jwtUtil.parseRefreshToken(refreshToken.token()).userId());
        assertThrows(JwtException.class, () -> jwtUtil.parseRefreshToken(accessToken));
        assertThrows(JwtException.class, () -> jwtUtil.parseAccessToken(refreshToken.token()));
    }
}

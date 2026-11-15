package com.pup.bataan.cosmos.security;

import com.pup.bataan.cosmos.entity.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;
    private Admin admin;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(
                jwtService, "secretKey", Base64.getEncoder().encodeToString(new byte[32])
        );
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 60_000L);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", 600_000L);

        admin = Admin.builder()
                .id(1L)
                .username("admin")
                .email("admin@example.com")
                .password("encoded-password")
                .fullName("Admin")
                .role(Admin.Role.ADMIN)
                .enabled(true)
                .build();
    }

    @Test
    void isTokenValid_rejectsTokensIssuedBeforePasswordReset() {
        String oldRefreshToken = jwtService.generateRefreshToken(admin);

        admin.setTokenVersion(admin.getTokenVersion() + 1);
        String newRefreshToken = jwtService.generateRefreshToken(admin);

        assertFalse(jwtService.isTokenValid(oldRefreshToken, admin));
        assertTrue(jwtService.isTokenValid(newRefreshToken, admin));
    }
}

package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.dto.request.LoginRequest;
import com.pup.bataan.cosmos.dto.response.AuthResponse;
import com.pup.bataan.cosmos.entity.Admin;
import com.pup.bataan.cosmos.repository.AdminRepository;
import com.pup.bataan.cosmos.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // ── Login ─────────────────────────────────────────────────
    public AuthResponse login(LoginRequest request,
                               HttpServletResponse response) {

        // Authenticate credentials
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        // Load user
        Admin admin = adminRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() ->
                new UsernameNotFoundException("User not found.")
            );

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(admin);
        String refreshToken = jwtService.generateRefreshToken(admin);

        // Set refresh token as HttpOnly cookie
        setRefreshTokenCookie(response, refreshToken);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);
        authResponse.setUsername(admin.getUsername());
        authResponse.setFullName(admin.getFullName());
        authResponse.setEmail(admin.getEmail());
        authResponse.setRole(admin.getRole().name());
        return authResponse;
    }

    // ── Refresh Token ─────────────────────────────────────────
    public AuthResponse refresh(HttpServletRequest request,
                                 HttpServletResponse response) {

        // Read refresh token from HttpOnly cookie
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            throw new RuntimeException("Refresh token not found.");
        }

        String username = jwtService.extractUsername(refreshToken);

        Admin admin = adminRepository
            .findByUsername(username)
            .orElseThrow(() ->
                new UsernameNotFoundException("User not found.")
            );

        if (!jwtService.isTokenValid(refreshToken, admin)) {
            throw new RuntimeException("Refresh token is invalid or expired.");
        }

        // Issue new access token
        String newAccessToken = jwtService.generateAccessToken(admin);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(newAccessToken);
        authResponse.setUsername(admin.getUsername());
        authResponse.setFullName(admin.getFullName());
        authResponse.setEmail(admin.getEmail());
        authResponse.setRole(admin.getRole().name());
        return authResponse;
    }

    // ── Logout ────────────────────────────────────────────────
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0);  // delete cookie
        response.addCookie(cookie);
    }

    // ── Helpers ───────────────────────────────────────────────
    private void setRefreshTokenCookie(HttpServletResponse response,
                                        String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);       // not accessible by JS
        cookie.setSecure(false);        // set to true in production
        cookie.setPath("/");
        cookie.setMaxAge((int)(refreshTokenExpiration / 1000));
        response.addCookie(cookie);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> "refresh_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
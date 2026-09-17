package com.pup.bataan.cosmos.controller;

import com.pup.bataan.cosmos.dto.request.LoginRequest;
import com.pup.bataan.cosmos.dto.response.AuthResponse;
import com.pup.bataan.cosmos.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    // POST /api/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.refresh(request, response));
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok("Logged out successfully.");
    }

    // GET /api/auth/me — get current logged-in user info
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(
            HttpServletRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.refresh(request, response));
    }
}
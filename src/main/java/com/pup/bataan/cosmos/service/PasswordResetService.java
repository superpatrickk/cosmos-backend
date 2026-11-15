package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.entity.Admin;
import com.pup.bataan.cosmos.entity.PasswordResetToken;
import com.pup.bataan.cosmos.repository.AdminRepository;
import com.pup.bataan.cosmos.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Duration TOKEN_LIFETIME = Duration.ofMinutes(30);
    private static final Duration REQUEST_COOLDOWN = Duration.ofMinutes(1);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AdminRepository adminRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${cosmos.frontend-url}")
    private String frontendUrl;

    @Transactional
    public void requestPasswordReset(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        Admin admin = adminRepository.findByEmailIgnoreCase(normalizedEmail).orElse(null);
        if (admin == null) {
            return;
        }

        Instant now = Instant.now();
        if (tokenRepository.existsByAdmin_IdAndCreatedAtAfter(
                admin.getId(), now.minus(REQUEST_COOLDOWN))) {
            return;
        }

        tokenRepository.deleteByAdmin_Id(admin.getId());

        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setAdmin(admin);
        resetToken.setTokenHash(hashToken(rawToken));
        resetToken.setCreatedAt(now);
        resetToken.setExpiresAt(now.plus(TOKEN_LIFETIME));
        tokenRepository.save(resetToken);

        String resetUrl = frontendUrl.replaceAll("/+$", "")
                + "/reset-password?token=" + rawToken;
        emailService.sendPasswordResetEmail(admin.getEmail(), resetUrl);
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        if (newPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password cannot exceed 72 bytes.");
        }

        Instant now = Instant.now();
        PasswordResetToken resetToken = tokenRepository
                .findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(hashToken(rawToken), now)
                .orElseThrow(() -> new IllegalArgumentException(
                        "This password reset link is invalid or has expired."
                ));

        Admin admin = resetToken.getAdmin();
        admin.setPassword(passwordEncoder.encode(newPassword));
        admin.setTokenVersion(admin.getTokenVersion() + 1);
        adminRepository.save(admin);

        var activeTokens = tokenRepository.findAllByAdmin_IdAndUsedAtIsNull(admin.getId());
        activeTokens.forEach(token -> token.setUsedAt(now));
        tokenRepository.saveAll(activeTokens);
    }

    private String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }
}

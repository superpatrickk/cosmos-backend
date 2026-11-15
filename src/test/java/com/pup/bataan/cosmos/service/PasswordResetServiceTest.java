package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.entity.Admin;
import com.pup.bataan.cosmos.entity.PasswordResetToken;
import com.pup.bataan.cosmos.repository.AdminRepository;
import com.pup.bataan.cosmos.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                passwordResetService, "frontendUrl", "http://localhost:5173"
        );
    }

    @Test
    void requestPasswordReset_sendsTokenToMatchingAdminEmail() {
        Admin admin = admin();
        when(adminRepository.findByEmailIgnoreCase("admin@example.com"))
                .thenReturn(Optional.of(admin));
        when(tokenRepository.existsByAdmin_IdAndCreatedAtAfter(any(), any()))
                .thenReturn(false);

        passwordResetService.requestPasswordReset(" ADMIN@example.com ");

        ArgumentCaptor<PasswordResetToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertEquals(64, savedToken.getTokenHash().length());
        assertNotNull(savedToken.getExpiresAt());

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendPasswordResetEmail(
                org.mockito.ArgumentMatchers.eq(admin.getEmail()), urlCaptor.capture()
        );
        String rawToken = urlCaptor.getValue().substring(
                urlCaptor.getValue().indexOf("?token=") + "?token=".length()
        );
        assertEquals(43, rawToken.length());
        assertEquals(hash(rawToken), savedToken.getTokenHash());
    }

    @Test
    void requestPasswordReset_doesNotSendEmailForUnknownAddress() {
        when(adminRepository.findByEmailIgnoreCase("unknown@example.com"))
                .thenReturn(Optional.empty());

        passwordResetService.requestPasswordReset("unknown@example.com");

        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
    }

    @Test
    void resetPassword_updatesPasswordAndInvalidatesTokens() {
        Admin admin = admin();
        PasswordResetToken token = new PasswordResetToken();
        token.setAdmin(admin);
        token.setTokenHash(hash("valid-token"));
        token.setExpiresAt(Instant.now().plusSeconds(600));
        when(tokenRepository.findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(
                anyString(), any(Instant.class)
        )).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-password");
        when(tokenRepository.findAllByAdmin_IdAndUsedAtIsNull(admin.getId()))
                .thenReturn(List.of(token));

        passwordResetService.resetPassword("valid-token", "new-password");

        assertEquals("encoded-password", admin.getPassword());
        assertEquals(1, admin.getTokenVersion());
        assertNotNull(token.getUsedAt());
        verify(adminRepository).save(admin);
        verify(tokenRepository).saveAll(List.of(token));
    }

    @Test
    void resetPassword_rejectsExpiredOrUnknownToken() {
        when(tokenRepository.findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(
                anyString(), any(Instant.class)
        )).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> passwordResetService.resetPassword("invalid-token", "new-password")
        );
        verify(adminRepository, never()).save(any(Admin.class));
    }

    private Admin admin() {
        return Admin.builder()
                .id(1L)
                .username("admin")
                .email("admin@example.com")
                .password("old-password")
                .fullName("Admin")
                .role(Admin.Role.ADMIN)
                .enabled(true)
                .build();
    }

    private String hash(String token) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(token.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new AssertionError(exception);
        }
    }
}

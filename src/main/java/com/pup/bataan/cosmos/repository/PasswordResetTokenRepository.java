package com.pup.bataan.cosmos.repository;

import com.pup.bataan.cosmos.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(
            String tokenHash,
            Instant expiresAt
    );

    boolean existsByAdmin_IdAndCreatedAtAfter(Long adminId, Instant createdAt);

    void deleteByAdmin_Id(Long adminId);

    List<PasswordResetToken> findAllByAdmin_IdAndUsedAtIsNull(Long adminId);
}

package com.zion.zion_center.service;

import com.zion.zion_center.entity.RefreshToken;
import com.zion.zion_center.entity.User;
import com.zion.zion_center.exception.BadRequestException;
import com.zion.zion_center.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    /** Create a new refresh token for the given user and return the raw (unhashed) value. */
    public String create(User user) {
        String raw = generateRaw();
        repository.save(RefreshToken.builder()
                .tokenHash(hash(raw))
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .build());
        return raw;
    }

    /**
     * Validate the raw token and return its DB record.
     * Throws {@link BadRequestException} if the token is unknown or expired.
     */
    @Transactional(readOnly = true)
    public RefreshToken validate(String raw) {
        RefreshToken token = repository.findByTokenHash(hash(raw))
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            repository.delete(token);
            throw new BadRequestException("Refresh token has expired, please log in again");
        }
        return token;
    }

    /** Delete the refresh token identified by the raw value (used on logout). */
    public void revoke(String raw) {
        repository.deleteByTokenHash(hash(raw));
    }

    /** Delete every refresh token belonging to the user (e.g. force-logout all devices). */
    public void revokeAll(User user) {
        repository.deleteAllByUser(user);
    }

    // ------------------------------------------------------------------ helpers

    private String generateRaw() {
        // 64 hex chars = 256 bits of randomness
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}

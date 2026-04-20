package com.zion.zion_center.repository;

import com.zion.zion_center.entity.RefreshToken;
import com.zion.zion_center.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteByTokenHash(String tokenHash);

    void deleteAllByUser(User user);
}

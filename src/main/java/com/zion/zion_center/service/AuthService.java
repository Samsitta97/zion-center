package com.zion.zion_center.service;

import com.zion.zion_center.dto.auth.LoginRequest;
import com.zion.zion_center.dto.auth.LoginResponse;
import com.zion.zion_center.dto.auth.RefreshRequest;
import com.zion.zion_center.dto.auth.RefreshResponse;
import com.zion.zion_center.entity.RefreshToken;
import com.zion.zion_center.entity.User;
import com.zion.zion_center.exception.BadRequestException;
import com.zion.zion_center.repository.UserRepository;
import com.zion.zion_center.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login failed — unknown email: {}", request.email());
                    return new BadRequestException("Invalid email or password");
                });

        if (!user.isActive()) {
            log.warn("Login failed — inactive account: {}", request.email());
            throw new BadRequestException("Account is inactive");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Login failed — wrong password: {}", request.email());
            throw new BadRequestException("Invalid email or password");
        }

        log.info("Login successful: email={}, role={}", user.getEmail(), user.getRole());

        String accessToken  = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = refreshTokenService.create(user);

        return new LoginResponse(accessToken, refreshToken, user.getName(), user.getEmail(), user.getRole().name());
    }

    @Transactional
    public RefreshResponse refresh(RefreshRequest request) {
        RefreshToken existing = refreshTokenService.validate(request.refreshToken());
        User user = existing.getUser();

        // Token rotation: revoke the used token and issue a new one
        refreshTokenService.revoke(request.refreshToken());
        String newRefreshToken = refreshTokenService.create(user);
        String newAccessToken  = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return new RefreshResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenService.revoke(request.refreshToken());
    }
}

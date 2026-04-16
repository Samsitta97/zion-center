package com.zion.zion_center.service;

import com.zion.zion_center.dto.auth.LoginRequest;
import com.zion.zion_center.dto.auth.LoginResponse;
import com.zion.zion_center.entity.User;
import com.zion.zion_center.exception.BadRequestException;
import com.zion.zion_center.repository.UserRepository;
import com.zion.zion_center.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.isActive()) {
            throw new BadRequestException("Account is inactive");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }
}

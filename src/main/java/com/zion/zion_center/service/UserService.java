package com.zion.zion_center.service;

import com.zion.zion_center.dto.user.ChangePasswordRequest;
import com.zion.zion_center.dto.user.UserRequest;
import com.zion.zion_center.dto.user.UserResponse;
import com.zion.zion_center.dto.user.UserUpdateRequest;
import com.zion.zion_center.entity.User;
import com.zion.zion_center.exception.AccessDeniedException;
import com.zion.zion_center.exception.BadRequestException;
import com.zion.zion_center.exception.ResourceNotFoundException;
import com.zion.zion_center.mapper.UserMapper;
import com.zion.zion_center.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse getById(Long id) {
        return userMapper.toResponse(findById(id));
    }

    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email is already in use");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(User.Role.valueOf(request.role().toUpperCase()))
                .build();

        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = findById(id);

        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email is already in use");
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(User.Role.valueOf(request.role().toUpperCase()));
        user.setActive(request.active());

        return userMapper.toResponse(userRepository.save(user));
    }

    public void changePassword(Long id, String authenticatedEmail, ChangePasswordRequest request) {
        User user = findById(id);

        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new AccessDeniedException("You can only change your own password");
        }

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Old password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}

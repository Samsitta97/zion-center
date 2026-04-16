package com.zion.zion_center.service;

import com.zion.zion_center.dto.sharedlink.SharedLinkRequest;
import com.zion.zion_center.dto.sharedlink.SharedLinkResponse;
import com.zion.zion_center.entity.Lesson;
import com.zion.zion_center.entity.SharedLink;
import com.zion.zion_center.entity.User;
import com.zion.zion_center.exception.AccessDeniedException;
import com.zion.zion_center.exception.ResourceNotFoundException;
import com.zion.zion_center.mapper.SharedLinkMapper;
import com.zion.zion_center.repository.LessonRepository;
import com.zion.zion_center.repository.SharedLinkRepository;
import com.zion.zion_center.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SharedLinkService {

    private final SharedLinkRepository sharedLinkRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final SharedLinkMapper sharedLinkMapper;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public SharedLinkResponse generate(SharedLinkRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Lesson lesson = lessonRepository.findById(request.lessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + request.lessonId()));

        SharedLink link = SharedLink.builder()
                .lesson(lesson)
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(request.expiresAt())
                .maxViews(request.maxViews())
                .viewCount(0)
                .isActive(true)
                .build();

        return sharedLinkMapper.toResponse(sharedLinkRepository.save(link), baseUrl);
    }

    public List<SharedLinkResponse> getMyLinks(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return sharedLinkRepository.findByUserId(user.getId()).stream()
                .map(l -> sharedLinkMapper.toResponse(l, baseUrl))
                .toList();
    }

    public void deactivate(Long id, String email) {
        SharedLink link = findLinkOwnedBy(id, email);
        link.setActive(false);
        sharedLinkRepository.save(link);
    }

    public void activate(Long id, String email) {
        SharedLink link = findLinkOwnedBy(id, email);
        link.setActive(true);
        sharedLinkRepository.save(link);
    }

    private SharedLink findLinkOwnedBy(Long id, String email) {
        SharedLink link = sharedLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Link not found: " + id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!link.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not your link");
        }

        return link;
    }
}

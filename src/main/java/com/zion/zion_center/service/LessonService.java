package com.zion.zion_center.service;

import com.zion.zion_center.dto.lesson.LessonRequest;
import com.zion.zion_center.dto.lesson.LessonResponse;
import com.zion.zion_center.entity.Category;
import com.zion.zion_center.entity.Class;
import com.zion.zion_center.entity.Lesson;
import com.zion.zion_center.entity.User;
import com.zion.zion_center.exception.AccessDeniedException;
import com.zion.zion_center.exception.ResourceNotFoundException;
import com.zion.zion_center.mapper.LessonMapper;
import com.zion.zion_center.repository.CategoryRepository;
import com.zion.zion_center.repository.ClassRepository;
import com.zion.zion_center.repository.LessonRepository;
import com.zion.zion_center.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LessonService {

    private static final Pattern YOUTUBE_ID_PATTERN =
            Pattern.compile("(?:v=|youtu\\.be/|embed/)([A-Za-z0-9_-]{11})");

    private final LessonRepository lessonRepository;
    private final ClassRepository classRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LessonMapper lessonMapper;

    public List<LessonResponse> getByClassId(Long classId, String email) {
        User user = findUserByEmail(email);
        if (isNotAdmin(user)) {
            if (!classRepository.existsByIdAndUserId(classId, user.getId())) {
                throw new AccessDeniedException("You do not have access to this lesson");
            }
        }
        return lessonRepository.findByAClassId(classId).stream()
                .map(lessonMapper::toResponse)
                .toList();
    }

    public List<LessonResponse> getByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found: " + categoryId);
        }
        return lessonRepository.findByCategoryId(categoryId).stream()
                .map(lessonMapper::toResponse)
                .toList();
    }

    public LessonResponse getById(Long id, String email) {
        Lesson lesson = findOrThrow(id);
        User user = findUserByEmail(email);
        if (isNotAdmin(user)) {
            verifyClassOwnership(lesson.getAClass().getId(), user);
        }
        return lessonMapper.toResponse(lesson);
    }

    public LessonResponse create(LessonRequest request, String email) {
        User user = findUserByEmail(email);
        Class aClass = classRepository.findById(request.classId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + request.classId()));
        if (isNotAdmin(user)) {
            verifyClassOwnership(aClass.getId(), user);
        }

        Category category = resolveCategory(request.categoryId());

        Lesson lesson = Lesson.builder()
                .aClass(aClass)
                .category(category)
                .title(request.title())
                .description(request.description())
                .youtubeUrl(request.youtubeUrl())
                .youtubeVideoId(extractYoutubeId(request.youtubeUrl()))
                .durationSeconds(request.durationSeconds())
                .isActive(true)
                .build();

        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    public LessonResponse update(Long id, LessonRequest request, String email) {
        Lesson lesson = findOrThrow(id);
        User user = findUserByEmail(email);
        if (isNotAdmin(user)) {
            verifyClassOwnership(lesson.getAClass().getId(), user);
        }

        if (request.classId() != null) {
            Class aClass = classRepository.findById(request.classId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + request.classId()));
            if (isNotAdmin(user)) {
                verifyClassOwnership(aClass.getId(), user);
            }
            lesson.setAClass(aClass);
        }

        lesson.setCategory(resolveCategory(request.categoryId()));
        lesson.setTitle(request.title());
        lesson.setDescription(request.description());
        lesson.setYoutubeUrl(request.youtubeUrl());
        lesson.setYoutubeVideoId(extractYoutubeId(request.youtubeUrl()));
        lesson.setDurationSeconds(request.durationSeconds());
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    public void delete(Long id, String email) {
        Lesson lesson = findOrThrow(id);
        User user = findUserByEmail(email);
        if (isNotAdmin(user)) {
            verifyClassOwnership(lesson.getAClass().getId(), user);
        }
        lessonRepository.deleteById(id);
    }

    private Lesson findOrThrow(Long id) {
        return lessonRepository.findByIdFetched(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private boolean isNotAdmin(User user) {
        return user.getRole() != User.Role.ADMIN;
    }

    private void verifyClassOwnership(Long classId, User user) {
        if (!classRepository.existsByIdAndUserId(classId, user.getId())) {
            throw new AccessDeniedException("You do not have access to this lesson");
        }
    }

    private Category resolveCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    private String extractYoutubeId(String url) {
        if (url == null) return null;
        Matcher matcher = YOUTUBE_ID_PATTERN.matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }
}

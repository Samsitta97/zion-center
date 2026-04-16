package com.zion.zion_center.service;

import com.zion.zion_center.dto.classdto.ClassRequest;
import com.zion.zion_center.dto.classdto.ClassResponse;
import com.zion.zion_center.entity.Category;
import com.zion.zion_center.entity.Class;
import com.zion.zion_center.entity.User;
import com.zion.zion_center.exception.AccessDeniedException;
import com.zion.zion_center.exception.ResourceNotFoundException;
import com.zion.zion_center.mapper.ClassMapper;
import com.zion.zion_center.repository.CategoryRepository;
import com.zion.zion_center.repository.ClassRepository;
import com.zion.zion_center.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ClassMapper classMapper;

    public List<ClassResponse> getAll(String email) {
        User user = findUserByEmail(email);
        List<Class> classes = isNotAdmin(user)
                ? classRepository.findByUserId(user.getId())
                : classRepository.findAll();
        return classes.stream().map(classMapper::toResponse).toList();
    }

    public ClassResponse getById(Long id, String email) {
        Class aClass = findOrThrow(id);
        User user = findUserByEmail(email);
        if (isNotAdmin(user)) {
            verifyOwnership(aClass.getId(), user);
        }
        return classMapper.toResponse(aClass);
    }

    public ClassResponse create(ClassRequest request, String email) {
        User user = findUserByEmail(email);

        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.categoryId()));
        }

        Class aClass = Class.builder()
                .user(user)
                .category(category)
                .title(request.title())
                .description(request.description())
                .classDate(request.classDate())
                .status(Class.Status.active)
                .build();

        return classMapper.toResponse(classRepository.save(aClass));
    }

    public ClassResponse update(Long id, ClassRequest request, String email) {
        Class aClass = findOrThrow(id);
        User user = findUserByEmail(email);
        if (isNotAdmin(user)) {
            verifyOwnership(aClass.getId(), user);
        }

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.categoryId()));
            aClass.setCategory(category);
        }

        aClass.setTitle(request.title());
        aClass.setDescription(request.description());
        aClass.setClassDate(request.classDate());
        return classMapper.toResponse(classRepository.save(aClass));
    }

    public void delete(Long id, String email) {
        Class aClass = findOrThrow(id);
        User user = findUserByEmail(email);
        if (isNotAdmin(user)) {
            verifyOwnership(aClass.getId(), user);
        }
        classRepository.deleteById(id);
    }

    private Class findOrThrow(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private boolean isNotAdmin(User user) {
        return user.getRole() != User.Role.ADMIN;
    }

    private void verifyOwnership(Long classId, User user) {
        if (!classRepository.existsByIdAndUserId(classId, user.getId())) {
            throw new AccessDeniedException("You do not have access to this class");
        }
    }
}

package com.zion.zion_center.service;

import com.zion.zion_center.dto.category.CategoryRequest;
import com.zion.zion_center.dto.category.CategoryResponse;
import com.zion.zion_center.entity.Category;
import com.zion.zion_center.entity.User;
import com.zion.zion_center.exception.ResourceNotFoundException;
import com.zion.zion_center.mapper.CategoryMapper;
import com.zion.zion_center.repository.CategoryRepository;
import com.zion.zion_center.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse create(CategoryRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .createdBy(user)
                .build();

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));

        category.setName(request.name());
        category.setDescription(request.description());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }
}

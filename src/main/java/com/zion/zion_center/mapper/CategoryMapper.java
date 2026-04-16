package com.zion.zion_center.mapper;

import com.zion.zion_center.dto.category.CategoryResponse;
import com.zion.zion_center.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);
}

package com.zion.zion_center.mapper;

import com.zion.zion_center.dto.classdto.ClassResponse;
import com.zion.zion_center.entity.Class;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClassMapper {

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "status", expression = "java(aClass.getStatus().name())")
    ClassResponse toResponse(Class aClass);
}

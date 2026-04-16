package com.zion.zion_center.mapper;

import com.zion.zion_center.dto.lesson.LessonResponse;
import com.zion.zion_center.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    @Mapping(target = "classId",    source = "AClass.id")
    @Mapping(target = "classTitle", source = "AClass.title")
    @Mapping(target = "isActive",   source = "active")
    LessonResponse toResponse(Lesson lesson);
}

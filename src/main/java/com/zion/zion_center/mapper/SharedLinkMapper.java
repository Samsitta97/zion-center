package com.zion.zion_center.mapper;

import com.zion.zion_center.dto.sharedlink.SharedLinkResponse;
import com.zion.zion_center.entity.SharedLink;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SharedLinkMapper {

    @Mapping(target = "lessonId",    source = "lesson.id")
    @Mapping(target = "lessonTitle", source = "lesson.title")
    @Mapping(target = "isActive",    source = "active")
    @Mapping(target = "watchUrl",    expression = "java(baseUrl + \"/api/watch/\" + sharedLink.getToken())")
    SharedLinkResponse toResponse(SharedLink sharedLink, @Context String baseUrl);
}

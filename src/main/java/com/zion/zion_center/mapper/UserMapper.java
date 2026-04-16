package com.zion.zion_center.mapper;

import com.zion.zion_center.dto.user.UserResponse;
import com.zion.zion_center.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "active", expression = "java(user.isActive())")
    UserResponse toResponse(User user);
}

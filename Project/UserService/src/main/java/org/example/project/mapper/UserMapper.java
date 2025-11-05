package org.example.project.mapper;

import org.example.project.dto.UserDto;
import org.example.project.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto dto);
    UserDto toDto(User entity);
    void updateEntityFromDto(UserDto dto, @MappingTarget User entity);
}
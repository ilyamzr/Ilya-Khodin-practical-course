package org.example.project.mapper;

import org.example.project.dto.CardInfoDto;
import org.example.project.entity.CardInfo;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {
    @Mapping(target = "user", ignore = true)
    CardInfo toEntity(CardInfoDto dto);

    CardInfoDto toDto(CardInfo entity);

    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(CardInfoDto dto, @MappingTarget CardInfo entity);

}
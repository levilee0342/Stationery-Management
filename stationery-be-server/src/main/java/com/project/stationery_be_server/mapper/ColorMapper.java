package com.project.stationery_be_server.mapper;

import com.project.stationery_be_server.dto.response.ColorResponse;
import com.project.stationery_be_server.entity.Color;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ColorMapper {
    ColorResponse toColorResponse(Color color);
}

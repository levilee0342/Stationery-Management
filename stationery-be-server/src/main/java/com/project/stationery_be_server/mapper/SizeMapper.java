package com.project.stationery_be_server.mapper;

import com.project.stationery_be_server.dto.response.SizeResponse;
import com.project.stationery_be_server.dto.response.UserResponse;
import com.project.stationery_be_server.entity.Size;
import com.project.stationery_be_server.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SizeMapper {
    SizeResponse toSizeResponse(Size user);
}

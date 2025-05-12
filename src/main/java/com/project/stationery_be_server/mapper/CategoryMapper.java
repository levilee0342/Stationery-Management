package com.project.stationery_be_server.mapper;

import com.project.stationery_be_server.dto.response.CategoryResponse;
import com.project.stationery_be_server.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(source = "categoryId", target = "categoryId")
    @Mapping(source = "categoryName", target = "categoryName")
    @Mapping(source = "icon", target = "icon")
    @Mapping(source = "bgColor", target = "bgColor")
    CategoryResponse toCategoryResponse(Category category);
}

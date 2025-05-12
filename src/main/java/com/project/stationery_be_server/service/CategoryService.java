package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.CategoryRequest;
import com.project.stationery_be_server.dto.response.CategoryResponse;
import com.project.stationery_be_server.entity.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    Category createCategory(CategoryRequest request);
    Category updateCategory(String categoryId, CategoryRequest request);
    void deleteCategory(String categoryId);
}

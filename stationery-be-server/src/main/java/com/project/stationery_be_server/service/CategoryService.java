package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.CategoryRequest;
import com.project.stationery_be_server.dto.response.CategoryResponse;
import com.project.stationery_be_server.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    Category createCategory(CategoryRequest request);
    Category updateCategory(String categoryId, CategoryRequest request);
    void deleteCategory(String categoryId);

    Page<CategoryResponse> getAllCategoriesPagination(Pageable pageable, String search);
}

package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.CategoryRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.CategoryResponse;
import com.project.stationery_be_server.entity.Category;
import com.project.stationery_be_server.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    // Get all categories
    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .message("Categories retrieved successfully")
                .result(categoryService.getAllCategories())
                .build();
    }

    // Create a new category
    @PostMapping
    public ApiResponse<Category> createCategory(@RequestBody CategoryRequest request) {
        Category createdCategory = categoryService.createCategory(request);
        return ApiResponse.<Category>builder()
                .message("Category created successfully")
                .result(createdCategory)
                .build();
    }

    // Update an existing category
    @PutMapping("/{categoryId}")
    public ApiResponse<Category> updateCategory(
            @PathVariable String categoryId,
            @RequestBody CategoryRequest request) {
        Category updatedCategory = categoryService.updateCategory(categoryId, request);
        return ApiResponse.<Category>builder()
                .message("Category updated successfully")
                .result(updatedCategory)
                .build();
    }

    // Delete a category
    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.<Void>builder()
                .message("Category deleted successfully")
                .build();
    }
}
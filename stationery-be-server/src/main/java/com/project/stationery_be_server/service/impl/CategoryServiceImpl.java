package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.Error.AuthErrorCode;
import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.CategoryRequest;
import com.project.stationery_be_server.dto.response.CategoryResponse;
import com.project.stationery_be_server.dto.response.SizeResponse;
import com.project.stationery_be_server.entity.Category;
import com.project.stationery_be_server.entity.Size;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.mapper.CategoryMapper;
import com.project.stationery_be_server.repository.CategoryRepository;
import com.project.stationery_be_server.service.CategoryService;
import com.project.stationery_be_server.specification.CategorySpecification;
import com.project.stationery_be_server.specification.SizeSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    @Override
    @Transactional
    public Category createCategory(CategoryRequest request) {
        if (categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new AppException(NotExistedErrorCode.CATEGORY_NAME_EXITS);
        }

        Category category = new Category();
        category.setCategoryId(request.getCategoryId());
        category.setCategoryName(request.getCategoryName());
        category.setIcon(request.getIcon());
        category.setBgColor(request.getBgColor());

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(String categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.CATEGORY_NOT_FOUND));

        // Check if new category name is unique (excluding current category)
        if (!category.getCategoryName().equals(request.getCategoryName()) &&
                categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new AppException(NotExistedErrorCode.CATEGORY_NAME_EXITS);
        }

        category.setCategoryName(request.getCategoryName());
        category.setIcon(request.getIcon());
        category.setBgColor(request.getBgColor());

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.CATEGORY_NOT_FOUND));

        if (!category.getProducts().isEmpty()) {
            throw new AppException(AuthErrorCode.DELETE_CATEGORY_FAIL);
        }

        categoryRepository.delete(category);
    }

    @Override
    public Page<CategoryResponse> getAllCategoriesPagination(Pageable pageable, String search) {
        Specification<Category> spec = CategorySpecification.filterCategory(search);
        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);
        // Có thể thêm logic tính toán thêm ở đây
        List<CategoryResponse> userResponses = categoryPage.getContent().stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
        return new PageImpl<>(userResponses, pageable, categoryPage.getTotalElements());

    }

}
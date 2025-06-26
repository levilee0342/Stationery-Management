package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.response.CategoryResponse;

import java.util.List;

public interface SearchHistoryService {
    List<String> getTop10Keywords();
    void logKeyword(String keyword, String UserId);
    List<CategoryResponse> getTop8UserCategories();
    List<CategoryResponse> getTop24HotCategories();
}

package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.CategoryResponse;
import com.project.stationery_be_server.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    @Autowired
    private SearchHistoryService searchHistoryService;

    @GetMapping("/top-keywords")
    public ApiResponse<List<String>> getTop10Keywords() {
        return ApiResponse.<List<String>>builder()
                .result(searchHistoryService.getTop10Keywords())
                .build();
    }

    @GetMapping("/top-user-categories")
    public ApiResponse<List<CategoryResponse>> getTop8UserCategories() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(searchHistoryService.getTop8UserCategories())
                .build();
    }

    @GetMapping("/hot-categories")
    public ApiResponse<List<CategoryResponse>> getTop24HotCategories() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(searchHistoryService.getTop24HotCategories())
                .build();
    }
}
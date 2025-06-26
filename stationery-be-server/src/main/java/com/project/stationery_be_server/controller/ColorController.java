package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.ColorRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.ColorResponse;
import com.project.stationery_be_server.dto.response.ColorResponse;
import com.project.stationery_be_server.entity.Color;
import com.project.stationery_be_server.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/colors")
@RequiredArgsConstructor
public class ColorController {
    private final ColorService colorService;

    @PostMapping
    public ApiResponse<ColorResponse> createColor(@RequestBody ColorRequest colorRequest) {
        return ApiResponse.<ColorResponse>builder()
                .result(colorService.createColor(colorRequest))
                .build();
    }
    @GetMapping
    public ApiResponse<List<ColorResponse>> getAllColors() {
        return ApiResponse.<List<ColorResponse>>builder()
                .result(colorService.getAllColors())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ColorResponse> updateColor(@PathVariable String id, @RequestBody ColorRequest colorRequest) {
        return ApiResponse.<ColorResponse>builder()
                .result(colorService.updateColor(id, colorRequest))
                .build();
    }
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/admin/get-colors")
    public ApiResponse<Page<ColorResponse>> getAllColorsForAdmin(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int limit,
                                                                      @RequestParam(required = false) String search

    ) {
        // sử lý ở FE page 1 là BE page 0, page 2 là page 1, ..
        page = page <= 1 ? 0 : page - 1;
        Pageable pageable;

        pageable = PageRequest.of(page, limit);
        Page<ColorResponse> pageResult = colorService.getAllColorPagination(pageable, search);
        return ApiResponse.<Page<ColorResponse>>builder()
                .result(pageResult)
                .build();
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteColor(@PathVariable String id) {
        colorService.deleteColor(id);
        return ApiResponse.<Void>builder()
                .message("Color deleted successfully")
                .build();
    }
}

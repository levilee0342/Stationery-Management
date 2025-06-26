package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.SizeRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.SizeResponse;
import com.project.stationery_be_server.service.SizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sizes")
@RequiredArgsConstructor
public class SizeController {

    private final SizeService sizeService;

    @PostMapping
    public ApiResponse<SizeResponse> createSize(@RequestBody SizeRequest sizeRequest) {
        return ApiResponse.<SizeResponse>builder()
                .result(sizeService.createSize(sizeRequest))
                .build();
    }

    @GetMapping
    public ApiResponse<List<SizeResponse>> getAllSizes() {
        return ApiResponse.<List<SizeResponse>>builder()
                .result(sizeService.getAllSizes())
                .build();
    }
    @DeleteMapping
    @RequestMapping("/{id}")
    public ApiResponse<Void> deleteSize(@PathVariable String id) {
        sizeService.deleteSize(id);
        return ApiResponse.<Void>builder()
                .message("Size deleted successfully")
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<SizeResponse> updateSize(@PathVariable String id, @RequestBody SizeRequest sizeRequest) {
        return ApiResponse.<SizeResponse>builder()
                .result(sizeService.updateSize(id, sizeRequest))
                .build();
    }
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/admin/get-sizes")
    public ApiResponse<Page<SizeResponse>> getAllProductsForAdmin(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int limit,
                                                                  @RequestParam(required = false) String search

                                                                  ) {
        // sử lý ở FE page 1 là BE page 0, page 2 là page 1, ..
        page = page <= 1 ? 0 : page - 1;
        Pageable pageable;

        pageable = PageRequest.of(page, limit);
        Page<SizeResponse> pageResult = sizeService.getAllSizePagination(pageable, search);
        return ApiResponse.<Page<SizeResponse>>builder()
                .result(pageResult)
                .build();
    }
}

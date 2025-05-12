package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.SizeRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.SizeResponse;
import com.project.stationery_be_server.service.SizeService;
import lombok.RequiredArgsConstructor;
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

    @PutMapping("/{id}")
    public ApiResponse<SizeResponse> updateSize(@PathVariable String id, @RequestBody SizeRequest sizeRequest) {
        return ApiResponse.<SizeResponse>builder()
                .result(sizeService.updateSize(id, sizeRequest))
                .build();
    }
}

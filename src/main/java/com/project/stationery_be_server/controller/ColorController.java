package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.ColorRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.ColorResponse;
import com.project.stationery_be_server.entity.Color;
import com.project.stationery_be_server.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
}

package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.ColorRequest;
import com.project.stationery_be_server.dto.response.ColorResponse;
import com.project.stationery_be_server.dto.response.SizeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ColorService {
    ColorResponse createColor(ColorRequest request);
    List<ColorResponse> getAllColors();
    ColorResponse updateColor(String id, ColorRequest request);
    void deleteColor(String id);
    Page<ColorResponse> getAllColorPagination(Pageable pageable, String search);
}


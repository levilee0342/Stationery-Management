package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.ColorRequest;
import com.project.stationery_be_server.dto.response.ColorResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ColorService {
    ColorResponse createColor(ColorRequest request);
    List<ColorResponse> getAllColors();
    ColorResponse updateColor(String id, ColorRequest request);
}


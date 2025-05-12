package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.SizeRequest;
import com.project.stationery_be_server.dto.response.SizeResponse;

import java.util.List;

public interface SizeService {
    SizeResponse createSize(SizeRequest request);
    List<SizeResponse> getAllSizes();
    SizeResponse updateSize(String id, SizeRequest request);
}

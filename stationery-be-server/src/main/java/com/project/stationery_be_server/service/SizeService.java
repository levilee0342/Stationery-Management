package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.SizeRequest;
import com.project.stationery_be_server.dto.request.UserFilterRequest;
import com.project.stationery_be_server.dto.response.SizeResponse;
import com.project.stationery_be_server.dto.response.UserInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SizeService {
    SizeResponse createSize(SizeRequest request);
    List<SizeResponse> getAllSizes();
    SizeResponse updateSize(String id, SizeRequest request);
    void deleteSize(String id);
    Page<SizeResponse> getAllSizePagination(Pageable pageable,String search);

}

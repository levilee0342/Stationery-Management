package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.dto.request.SizeRequest;
import com.project.stationery_be_server.dto.response.SizeResponse;
import com.project.stationery_be_server.entity.Size;
import com.project.stationery_be_server.repository.SizeRepository;
import com.project.stationery_be_server.service.SizeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SizeServicelmpl implements SizeService {

    private final SizeRepository sizeRepository;

    public SizeServicelmpl(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    public SizeResponse createSize(SizeRequest request) {
        Size size = new Size();
        size.setSizeId(request.getSizeId());
        size.setName(request.getName());
        size.setPriority(request.getPriority());
        Size savedSize = sizeRepository.save(size);

        return new SizeResponse(savedSize.getSizeId(), savedSize.getName(), savedSize.getPriority());
    }

    @Override
    public List<SizeResponse> getAllSizes() {
        List<Size> sizes = sizeRepository.findAll();
        return sizes.stream()
                .map(size -> new SizeResponse(size.getSizeId(), size.getName(), size.getPriority()))
                .collect(Collectors.toList());
    }

    @Override
    public SizeResponse updateSize(String id, SizeRequest request) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found with ID: " + id));

        size.setName(request.getName());

        Size updatedSize = sizeRepository.save(size);
        return new SizeResponse(updatedSize.getSizeId(), updatedSize.getName(), updatedSize.getPriority());

    }
}

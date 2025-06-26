package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.dto.request.SizeRequest;
import com.project.stationery_be_server.dto.request.UserFilterRequest;
import com.project.stationery_be_server.dto.response.SizeResponse;
import com.project.stationery_be_server.dto.response.UserInfoResponse;
import com.project.stationery_be_server.entity.Size;
import com.project.stationery_be_server.entity.User;
import com.project.stationery_be_server.mapper.SizeMapper;
import com.project.stationery_be_server.repository.SizeRepository;
import com.project.stationery_be_server.service.SizeService;
import com.project.stationery_be_server.specification.SizeSpecification;
import com.project.stationery_be_server.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SizeServicelmpl implements SizeService {

    private final SizeRepository sizeRepository;
    private final SizeMapper sizeMapper;

    public SizeServicelmpl(SizeRepository sizeRepository, SizeMapper sizeMapper) {
        this.sizeRepository = sizeRepository;
        this.sizeMapper = sizeMapper;
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

    @Override
    public void deleteSize(String id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found with ID: " + id));
        if(!size.getProductDetails().isEmpty()) {
            throw new RuntimeException("Cannot delete size with ID: " + id + " because it is associated with product details.");
        }
        sizeRepository.delete(size);
    }

    @Override
    public Page<SizeResponse> getAllSizePagination(Pageable pageable, String search) {
            Specification<Size> spec = SizeSpecification.filterUsersForSize(search);
            Page<Size> sizePage = sizeRepository.findAll(spec, pageable);
            // Có thể thêm logic tính toán thêm ở đây
            List<SizeResponse> userResponses = sizePage.getContent().stream()
                    .map(sizeMapper::toSizeResponse)
                    .toList();
            return new PageImpl<>(userResponses, pageable, sizePage.getTotalElements());

    }
}

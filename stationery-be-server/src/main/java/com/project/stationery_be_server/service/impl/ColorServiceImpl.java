package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.dto.request.ColorRequest;
import com.project.stationery_be_server.dto.response.ColorResponse;
import com.project.stationery_be_server.dto.response.SizeResponse;
import com.project.stationery_be_server.entity.Color;
import com.project.stationery_be_server.entity.Size;
import com.project.stationery_be_server.mapper.ColorMapper;
import com.project.stationery_be_server.repository.ColorRepository;
import com.project.stationery_be_server.service.ColorService;
import com.project.stationery_be_server.specification.ColorSpecification;
import com.project.stationery_be_server.specification.SizeSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ColorServiceImpl implements ColorService {
    final ColorRepository colorRepository;
    ColorMapper colorMapper;

    public ColorResponse createColor(ColorRequest request) {
        Color color = new Color();
        color.setColorId(request.getColorId());
        color.setName(request.getName());
        color.setHex(request.getHex());

        Color savedColor = colorRepository.save(color);

        return new ColorResponse(savedColor.getColorId(), savedColor.getName(), savedColor.getHex());
    }

    @Override
    public List<ColorResponse> getAllColors() {
        List<Color> colors = colorRepository.findAll();
        return colors.stream()
                .map(color -> new ColorResponse(color.getColorId(), color.getName(), color.getHex()))
                .collect(Collectors.toList());
    }

    @Override
    public ColorResponse updateColor(String id, ColorRequest request) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found with ID: " + id));

        color.setName(request.getName());
        color.setHex(request.getHex());

        Color updatedColor = colorRepository.save(color);
        return new ColorResponse(updatedColor.getColorId(), updatedColor.getName(), updatedColor.getHex());
    }

    @Override
    public void deleteColor(String id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found with ID: " + id));
        if(color.getImages()!= null && !color.getImages().isEmpty()) {
            throw new RuntimeException("Cannot delete color");
        }
        colorRepository.delete(color);
    }

    @Override
    public Page<ColorResponse> getAllColorPagination(Pageable pageable, String search) {

        Specification<Color> spec = ColorSpecification.filterColor(search);
        Page<Color> sizePage = colorRepository.findAll(spec, pageable);
        // Có thể thêm logic tính toán thêm ở đây
        List<ColorResponse> userResponses = sizePage.getContent().stream()
                .map(colorMapper::toColorResponse)
                .toList();
        return new PageImpl<>(userResponses, pageable, sizePage.getTotalElements());

    }
}


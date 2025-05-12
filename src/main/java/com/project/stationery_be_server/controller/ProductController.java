package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.DeleteProductRequest;
import com.project.stationery_be_server.dto.request.ProductFilterRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.ColorSizeSlugResponse;
import com.project.stationery_be_server.dto.response.product.ProductDetailResponse;
import com.project.stationery_be_server.dto.response.product.ProductResponse;
import com.project.stationery_be_server.service.ProductService;
import com.project.stationery_be_server.service.SearchHistoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;
    SearchHistoryService searchHistoryService;

    @GetMapping
    public ApiResponse<Page<ProductResponse>> getAllProductsWithDefaultPD(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int limit,
                                                                          @RequestParam(required = false) String sortBy,
                                                                          @RequestParam(required = false) String minPrice,
                                                                          @RequestParam(required = false) String maxPrice,
                                                                          @RequestParam(required = false) String search,
                                                                          @RequestParam(required = false) String categoryId,
                                                                          @RequestParam(required = false) String totalRating
    ) {
        Sort sort = null;
        // sử lý ở FE page 1 là BE page 0, page 2 là page 1, ..
        page = page <= 1 ? 0 : page - 1;
        if (sortBy != null) {
            String[] parts = sortBy.split("(?<=-)|(?=-)"); // tách dấu tru trong chuoi
            //-abc
            sort = parts.length == 1 ? Sort.by(parts[0]).ascending() : Sort.by(parts[1]).descending();
        }
        Pageable pageable;
        if (sort != null) {
            pageable = PageRequest.of(page, limit, sort);

        } else {
            pageable = PageRequest.of(page, limit);
        }
        ProductFilterRequest filterRequest = ProductFilterRequest.builder()
                .categoryId(categoryId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .search(search)
                .totalRating(totalRating)
                .build();
        String userId = null;
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            userId = authentication.getName();
        }
        Page<ProductResponse> pageResult = productService.getAllProductWithDefaultPD(pageable, filterRequest);
        return ApiResponse.<Page<ProductResponse>>builder()
                .result(pageResult)
                .build();
    }

    @GetMapping("/get-products")
    public ApiResponse<Page<ProductResponse>> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int limit,
                                                             @RequestParam(required = false) String sortBy,
                                                             @RequestParam(required = false) String minPrice,
                                                             @RequestParam(required = false) String maxPrice,
                                                             @RequestParam(required = false) String search,
                                                             @RequestParam(required = false) String categoryId,
                                                             @RequestParam(required = false) String totalRating
    ) {
        Sort sort = null;
        // sử lý ở FE page 1 là BE page 0, page 2 là page 1, ..
        page = page <= 1 ? 0 : page - 1;
        if (sortBy != null) {
            String[] parts = sortBy.split("(?<=-)|(?=-)"); // tách dấu tru trong chuoi
            //-abc
            sort = parts.length == 1 ? Sort.by(parts[0]).ascending() : Sort.by(parts[1]).descending();
        }
        Pageable pageable;
        if (sort != null) {
            pageable = PageRequest.of(page, limit, sort);

        } else {
            pageable = PageRequest.of(page, limit);
        }
        ProductFilterRequest filterRequest = ProductFilterRequest.builder()
                .categoryId(categoryId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .search(search)
                .totalRating(totalRating)
                .build();
        Page<ProductResponse> pageResult = productService.getAllProducts(pageable, filterRequest);

        return ApiResponse.<Page<ProductResponse>>builder()
                .result(pageResult)
                .build();
    }

    @GetMapping("/product-detail/{productId}")
    public ApiResponse<List<ProductDetailResponse>> getProductDetailByProduct(@PathVariable String productId) {
        return ApiResponse.<List<ProductDetailResponse>>builder()
                .result(productService.getProductDetailByProduct(productId))
                .build();
    }

    @GetMapping("/{slug}")
    public ApiResponse<ProductResponse> getProductDetailProduct(@PathVariable String slug) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProductDetail(slug))
                .build();
    }

    @GetMapping("/color-size/{slug}")
    public ApiResponse<List<ColorSizeSlugResponse>> getColorSizeSlug(@PathVariable String slug) {
        return ApiResponse.<List<ColorSizeSlugResponse>>builder()
                .result(productService.fetchColorSizeSlug(slug))
                .build();
    }

    @DeleteMapping("/delete")
    public ApiResponse<String> deleteProduct(@RequestBody DeleteProductRequest request) {
        productService.deleteProduct(request);
        return ApiResponse.<String>builder()
                .result("Product deleted successfully")
                .build();
    }
}

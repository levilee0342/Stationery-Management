package com.project.stationery_be_server.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.DeleteProductDetailRequest;
import com.project.stationery_be_server.dto.request.UpdateProductDetailRequest;
import com.project.stationery_be_server.dto.response.product.ProductDetailResponse;
import com.project.stationery_be_server.dto.response.promotion.ProductDetailPromotion;
import com.project.stationery_be_server.entity.*;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.repository.*;
import com.project.stationery_be_server.service.ProductDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductDetailServiceImpl implements ProductDetailService {
    UserRepository userRepository;
    ProductDetailRepository productDetailRepository;
    ColorRepository colorRepository;
    SizeRepository sizeRepository;
    ProductPromotionRepository productPromotionRepository;
    PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    CartRepository cartRepository;
    ProductRepository productRepository;
    ImageRepository imageRepository;
    Cloudinary cloudinary;

    @Override
    public void deleteProductDetail(DeleteProductDetailRequest request) {
        var context = SecurityContextHolder.getContext();
        String userIdLogin = context.getAuthentication().getName();
        User user = userRepository.findById(userIdLogin)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        // admin moi dc xoa
        if (!user.getRole().getRoleName().equals("admin")) {
            throw new RuntimeException("You do not have permission to delete product details");
        }
        // kiem tra lien ket
        String detailId = request.getProductDetailId();
        ProductDetail productDetail = productDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Can not find the product details"));
        long promos = productPromotionRepository.countByProductDetail_ProductDetailId(detailId);
        long orders = purchaseOrderDetailRepository.countByProductDetail_ProductDetailId(detailId);
        long carts = cartRepository.countByProductDetail_ProductDetailId(detailId);
        long defaults = productRepository.countByProductDetail_ProductDetailId(detailId);

        if (promos > 0 || orders > 0 || carts > 0 || defaults > 0) {
            throw new IllegalStateException(
                    String.format(
                            "Cannot delete ProductDetail %s: %d promotions, %d order-lines, %d carts, %d default-pd references",
                            detailId, promos, orders, carts, defaults
                    )
            );
        }

        productDetailRepository.deleteById(detailId);
    }

    @Override
    @Transactional
    public ProductDetailResponse updateProductDetail(String pd, List<String> imgIdToUpdate, List<MultipartFile> images) {

        // 1. Chuyển chuỗi JSON thành DTO
        ObjectMapper mapper = new ObjectMapper();
        final UpdateProductDetailRequest form;
        try {
            form = mapper.readValue(pd, UpdateProductDetailRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 2. Tìm ProductDetail cần cập nhật
        ProductDetail productDetail = productDetailRepository.findByProductDetailId(form.getProductDetailId());
        if (productDetailRepository.existsBySlugAndProductDetailIdNot(form.getSlug(), form.getProductDetailId())) {
            throw new RuntimeException("Slug already existed: " + form.getSlug());
        }
        if(productDetailRepository.existsByNameAndProductDetailIdNot(form.getSlug(), form.getProductDetailId())){
            throw new RuntimeException("Product name already existed: " + form.getName());
        }
        // 3. Cập nhật các trường cơ bản
        productDetail.setName(form.getName());
        productDetail.setSlug(form.getSlug());
        productDetail.setOriginalPrice(form.getOriginalPrice());
        productDetail.setDiscountPrice(form.getDiscountPrice());
        productDetail.setAvailableQuantity(productDetail.getStockQuantity() - form.getStockQuantity() + productDetail.getAvailableQuantity());
        productDetail.setStockQuantity(form.getStockQuantity());
        Color color = colorRepository.findById(form.getColorId())
                .orElseThrow(() -> new RuntimeException("Color not found: " + form.getColorId()));
        productDetail.setColor(color);
        if(!form.getSizeId().isBlank()){
            Size size = sizeRepository.findById(form.getSizeId())
                    .orElseThrow(() -> new RuntimeException("Size not found: " + form.getSizeId()));
            productDetail.setSize(size);
        }

        // 4. Cập nhật hình ảnh
        if (form.getDeleteImages() != null && !form.getDeleteImages().isEmpty()) {
            for (String imageId : form.getDeleteImages()) {
                System.out.println("delete image with id: " + imageId);
                Image img = imageRepository.findById(imageId)
                        .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
                imageRepository.deleteById(imageId);
                String publicKey = extractPublicIdFromUrl(img.getUrl());
                deleteImageAsync(publicKey);
                // hoặc gọi đến Cloudinary/xóa trong S3 nếu dùng lưu ảnh bên ngoài
            }
        }

        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                if(file == null) continue;
                // lấy imgId muốn update
                String imgId =imgIdToUpdate.size()> i ?  imgIdToUpdate.get(i) : null;
                if (imgId != null && !imgId.isBlank()) { // update hình cũ thành mới
                    Image img = imageRepository.findById(imgId).orElseThrow(() -> new RuntimeException("Image not found with id: " + imgId));
                    String publicKey = extractPublicIdFromUrl(img.getUrl());
                    deleteImageAsync(publicKey);
                    Map uploadResult = null;
                    try {
                        uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String url = uploadResult.get("secure_url").toString();
                    img.setUrl(url);
                    imageRepository.save(img);
                }else{
                    try {
                        var uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                        String url = uploadResult.get("secure_url").toString();
                        Image image = Image.builder()
                                .url(url)
                                .priority(i)
                                .product(productDetail.getProduct())
                                .color(color)
                                .build();
                        imageRepository.save(image);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload image", e);
                    }
                }
            }
        }


        return null;
    }

    @Override
    public Boolean updateHiddenPD(String productDetailId, boolean hidden) {
        ProductDetail productDetail = productDetailRepository.findById(productDetailId)
                .orElseThrow(() -> new RuntimeException("Product detail not found with id: " + productDetailId));
        productDetail.setHidden(hidden);
        productDetailRepository.save(productDetail);
        return hidden;
    }

    @Override
    public List<ProductDetailPromotion> getAllProductDetailSimple() {
        List<ProductDetail> details = productDetailRepository.findAll();
        return details.stream()
                .map(d ->
                        ProductDetailPromotion.builder()
                                .productDetailId(d.getProductDetailId())
                                .name(d.getName())
                                .build()
                )
                .toList();

    }

    public void deleteImageAsync(String publicId) {
        new Thread(() -> {
            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (Exception e) {
                System.err.println("Failed to delete image: " + publicId);
            }
        }).start();
    }

    public String extractPublicIdFromUrl(String url) {
        if (url == null || !url.contains("/upload/")) {
            throw new IllegalArgumentException("Invalid Cloudinary URL");
        }

        String[] parts = url.split("/upload/");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Cannot extract publicId from URL");
        }

        String path = parts[1]; // v1748019306/slwle2jgfsfnaxohjcga.jpg
        String[] segments = path.split("/");

        String filename = segments[segments.length - 1];
        return filename.replaceAll("\\.\\w+$", ""); // remove extension like .jpg
    }

}

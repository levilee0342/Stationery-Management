package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.DeleteProductDetailRequest;
import com.project.stationery_be_server.entity.ProductDetail;
import com.project.stationery_be_server.entity.User;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.repository.*;
import com.project.stationery_be_server.service.ProductDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductDetailServiceImpl implements ProductDetailService {
    UserRepository userRepository;
    ProductDetailRepository productDetailRepository;
    ProductPromotionRepository productPromotionRepository;
    PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    CartRepository cartRepository;
    ProductRepository productRepository;

    @Override
    public void deleteProductDetail(DeleteProductDetailRequest request) {
        var context = SecurityContextHolder.getContext();
        String userIdLogin = context.getAuthentication().getName();
        User user = userRepository.findById(userIdLogin)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        // admin moi dc xoa
        if (!user.getRole().getRoleName().equals("admin")){
            throw new RuntimeException("You do not have permission to delete product details");
        }
        // kiem tra lien ket
        String detailId = request.getProductDetailId();
        ProductDetail productDetail = productDetailRepository.findById(detailId)
                .orElseThrow(()-> new RuntimeException("Can not find the product details"));
        long promos   = productPromotionRepository.countByProductDetail_ProductDetailId(detailId);
        long orders   = purchaseOrderDetailRepository.countByProductDetail_ProductDetailId(detailId);
        long carts    = cartRepository.countByProductDetail_ProductDetailId(detailId);
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
}

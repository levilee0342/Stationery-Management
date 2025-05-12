package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.AddCartItemRequest;
import com.project.stationery_be_server.dto.request.UpdateCartItemRequest;
import com.project.stationery_be_server.dto.response.CartResponse;
import com.project.stationery_be_server.entity.*;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.repository.*;
import com.project.stationery_be_server.service.CartService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements CartService {

    CartRepository cartRepository;
    UserRepository userRepository;
    ProductDetailRepository productDetailRepository;
    ProductPromotionRepository productPromotionRepository;
    ImageRepository imageRepository;

    // Lấy userId từ SecurityContext
    private String getCurrentUserId() {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        String userId = authentication.getName();
        System.out.println("Authentication.getName() returns: " + userId); // Log giá trị
        return userId;
    }

    // 1. Thêm hoặc tạo giỏ hàng (gộp createCart và addItemToCart)
    @Override
    @Transactional
    public CartResponse addItemToCart(AddCartItemRequest request) {
        String userId = getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        System.out.println("request.getProductDetailId() = " + request.getProductDetailId());
        ProductDetail productDetail =  productDetailRepository.findByProductDetailId(request.getProductDetailId());
        if(productDetail == null) {
            throw new IllegalArgumentException("Product variant not found");
        }
        if (productDetail.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock for this variant");
        }

        CartId cartId = new CartId(userId, productDetail.getProductDetailId());
        Optional<Cart> existingCart = cartRepository.findByCartId(cartId);

        Cart cart;
        System.out.println(" request.getQuantity() "+ request.getQuantity());
        if (existingCart.isPresent()) {
            cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + request.getQuantity());
        } else {
            cart = Cart.builder()
                    .cartId(cartId)
                    .user(user)
                    .productDetail(productDetail)
                    .quantity(request.getQuantity())
                    .createdAt(new Date())
                    .build();
        }

        cart = cartRepository.save(cart);
        return mapToCartResponse(cart);
    }

    @Autowired
    private EntityManager entityManager;

    // 2. Cập nhật item trong giỏ hàng (sử dụng AddCartItemRequest)
    @Override
    @Transactional
    public CartResponse updateItemInCart(String productDetailId, UpdateCartItemRequest request) {
        String userId = getCurrentUserId();

        // Tìm product detail
        ProductDetail productDetail = productDetailRepository.findByProductDetailId(productDetailId);
        if (productDetail == null) {
            throw new IllegalArgumentException("Product variant not found");
        }

        // Kiểm tra stock
        if (productDetail.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock for this variant");
        }

        // Tìm cart
        CartId cartId = new CartId(userId, productDetailId);
        Cart cart = cartRepository.findByCartId(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        // Cập nhật quantity
        cart.setQuantity(request.getQuantity());

        // Lưu cart
        cart = cartRepository.save(cart);

        // Trả về response
        return mapToCartResponse(cart);
    }

    // 3. Xóa item khỏi giỏ hàng
    @Override
    @Transactional
    public void removeItemFromCart(String productDetailId) {
        String userId = getCurrentUserId();

        CartId cartId = new CartId(userId, productDetailId);
        Cart cart = cartRepository.findByCartId(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        cartRepository.delete(cart);
    }


    // 4. Xem giỏ hàng của user
    @Override
    public List<CartResponse> viewCart() {
        String userId = getCurrentUserId();
        List<Cart> carts = cartRepository.findByUserId(userId);

        return carts.stream()
                .map(this::mapToCartResponse)
                .collect(Collectors.toList());
    }

    // 5. Xem tất cả giỏ hàng (admin)
    @Override
    public List<CartResponse> viewAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        return carts.stream()
                .map(this::mapToCartResponse)
                .collect(Collectors.toList());
    }

    private CartResponse mapToCartResponse(Cart cart) {
        ProductDetail productDetail = cart.getProductDetail();
        // Lấy ảnh đầu tiên (ưu tiên ảnh có priority thấp nhất nếu đã @OrderBy)
        String imageUrl = null;

        if (productDetail.getProduct() != null && productDetail.getColor() != null) {
            List<Image> images = imageRepository.findByProduct_ProductIdAndColor_ColorIdOrderByPriorityAsc(
                    productDetail.getProduct().getProductId(),
                    productDetail.getColor().getColorId()
            );
            if (!images.isEmpty()) {
                imageUrl = images.get(0).getUrl(); // lấy ảnh đầu tiên theo độ ưu tiên
            }
        }
        List<ProductPromotion> pm = productPromotionRepository.findValidPromotionForProductDetail(productDetail.getProductDetailId(), String.valueOf(productDetail.getDiscountPrice()));
        int discountValue = 0;
        int disCountPrice = productDetail.getDiscountPrice();
        if(pm != null && !pm.isEmpty()) {
            Promotion currentPromotion = pm.getFirst().getPromotion();
            if (currentPromotion.getDiscountType() == Promotion.DiscountType.PERCENTAGE) {
                // giam %
                int valueDisCount = (productDetail.getDiscountPrice() * currentPromotion.getDiscountValue()) / 100;
                if (currentPromotion.getMaxValue()!= null && valueDisCount > currentPromotion.getMaxValue()) { // neu so tien  vuot qua max value
                    discountValue = currentPromotion.getMaxValue();
                } else {
                    discountValue = valueDisCount;
                }
            } else {
                // giam theo gia tri
                discountValue = currentPromotion.getDiscountValue();
            }
            disCountPrice -= discountValue;
        }
        return  CartResponse.builder()
                .userId(cart.getUser().getUserId())
                .productId(productDetail.getProduct().getProductId())
                .productDetailId(productDetail.getProductDetailId())
                .productName(productDetail.getProduct().getName())
                .productPromotion(pm)
                .colorName(productDetail.getColor() != null ? productDetail.getColor().getName() : null)
                .sizeName(productDetail.getSize() != null ? productDetail.getSize().getName() : null)
                .quantity(cart.getQuantity())
                .originalPrice(productDetail.getOriginalPrice())
                .discountPrice(disCountPrice)
                .discountValue(discountValue)
                .createdAt(cart.getCreatedAt())
                .imageUrl(imageUrl)
                .build();
    }

    @Override
    public int calculateCartTotal() {
        String userId = getCurrentUserId();
        List<Cart> carts = cartRepository.findByUserId(userId);

        return carts.stream()
                .mapToInt(cart -> {
                    ProductDetail productDetail = cart.getProductDetail();
                    return productDetail.getDiscountPrice() * cart.getQuantity();
                })
                .sum();
    }
}
package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.DeletePromotionRequest;
import com.project.stationery_be_server.dto.response.ColorResponse;
import com.project.stationery_be_server.dto.response.promotion.ProductDetailPromotion;
import com.project.stationery_be_server.dto.response.promotion.PromotionResponse;
import com.project.stationery_be_server.dto.response.promotion.UserInPromotion;
import com.project.stationery_be_server.entity.*;
import com.project.stationery_be_server.dto.request.PromotionRequest;
import com.project.stationery_be_server.dto.request.UpdatePromotionRequest;
import com.project.stationery_be_server.entity.*;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.mapper.PromotionMapper;
import com.project.stationery_be_server.repository.*;
import com.project.stationery_be_server.service.NotificationService;
import com.project.stationery_be_server.service.PromotionService;
import com.project.stationery_be_server.specification.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromotionServiceImpl implements PromotionService {
    PromotionRepository promotionRepository;
    UserRepository userRepository;
    ProductPromotionRepository productPromotionRepository;
    UserPromotionRepository userPromotionRepository;
    ProductRepository productRepository;
    ProductDetailRepository productDetailRepository;
    private final PromotionMapper promotionMapper;
    private final NotificationService notificationService;

    @Override
    public BigDecimal applyPromotion(String promoCode, BigDecimal orderTotal, User user) {
        return null;
    }

    @Override
    public List<Promotion> getAvailablePromotions(User user, BigDecimal orderTotal) {
        return List.of();
    }

    @Override
    public BigDecimal calculateDiscount(Promotion promotion, BigDecimal orderTotal) {
        return null;
    }

    @Override
    @Transactional
    public void deletePromotion(DeletePromotionRequest request) {
        var context = SecurityContextHolder.getContext();
        String userIdLogin = context.getAuthentication().getName();
        User user = userRepository.findById(userIdLogin)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        // admin moi dc xoa
        if (!user.getRole().getRoleName().equals("admin")) {
            throw new RuntimeException("You do not have permission to delete users");
        }
        String promotionId = request.getPromotionId();
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        // Dem so luong su dung cua promotion
        int userUsage = promotionRepository.countUserPromotionUsage(promotionId);
        int productUsage = promotionRepository.countProductPromotionUsage(promotionId);
        if (userUsage > 0) {
            throw new RuntimeException("Cannot delete this promotion because it is currently being used by users.");
        }
        if (productUsage > 0) {
            throw new RuntimeException("Cannot delete this promotion because it is currently being used by products.");
        }
        promotionRepository.delete(promotion);

    }




    @Override
    @Transactional
    public void updatePromotion(UpdatePromotionRequest request) {

        String promotionId = request.getPromotionId();
        Promotion existingPromo = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion không tồn tại"));


        String newCode = request.getPromoCode().trim();
        if (!newCode.equalsIgnoreCase(existingPromo.getPromoCode())) {
            Optional<Promotion> other = promotionRepository.findByPromoCode(newCode);
            if (other.isPresent() && !other.get().getPromotionId().equals(promotionId)) {
                throw new RuntimeException("PromoCode đã tồn tại, vui lòng chọn mã khác");
            }
            existingPromo.setPromoCode(newCode);
        }

        existingPromo.setDiscountType(request.getDiscountType());
        existingPromo.setDiscountValue(request.getDiscountValue());
        existingPromo.setUsageLimit(request.getUsageLimit());
        existingPromo.setTempUsageLimit(request.getUsageLimit()); // nếu muốn đồng bộ tạm thời
        existingPromo.setMaxValue(request.getMaxValue());
        existingPromo.setMinOrderValue(request.getMinOrderValue());
        existingPromo.setStartDate(request.getStartDate());
        existingPromo.setEndDate(request.getEndDate());

        promotionRepository.save(existingPromo);
    }

    @Override
    @Transactional
    public Page<Promotion> getMyVouchers(Pageable pageable) {
        // 1. Lấy user hiện tại
        var context = SecurityContextHolder.getContext();
        String userIdLogin = context.getAuthentication().getName();
        User user = userRepository.findById(userIdLogin)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));

        // 2. Query ra Page<UserPromotion> theo User
        Page<UserPromotion> upPage = userPromotionRepository.findByUser(user, pageable);

        // 3. Map mỗi UserPromotion -> Promotion
        List<Promotion> promotions = upPage.getContent().stream()
                .map(UserPromotion::getPromotion)
                .collect(Collectors.toList());

        // 4. Trả về PageImpl<Promotion> với cùng pageable và tổng phần tử
        return new PageImpl<>(promotions, pageable, upPage.getTotalElements());
    }

    @Override
    @Transactional
    public Page<UserPromotion> getAllUserVouchers(Pageable pageable) {
        return userPromotionRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Page<PromotionResponse> getAllPromotion(Pageable pageable, String search) {
        Specification<Promotion> spec = PromotionSpecification.filterPromotion(search);
        Page<Promotion> promotionPage = promotionRepository.findAll(spec, pageable);
        // Có thể thêm logic tính toán thêm ở đây
        List<PromotionResponse> dtos = promotionPage.getContent().stream()
                .map(promotionMapper::toDto)
                .toList();
        return new PageImpl<>(dtos, pageable, promotionPage.getTotalElements());
    }

    @Override
    @Transactional
    public Promotion getPromotionById(String promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Promotion với id: " + promotionId));
    }

    @Override
    @Transactional
    public Page<Promotion> getPromotionsByUser(String userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại với id: " + userId));

        Page<UserPromotion> upPage = userPromotionRepository.findByUser(user, pageable);
        List<Promotion> promotions = upPage.getContent().stream()
                .map(UserPromotion::getPromotion)
                .collect(Collectors.toList());
        return new PageImpl<>(promotions, pageable, upPage.getTotalElements());
    }

    @Override
    @Transactional
    public Page<Promotion> getPromotionsByProduct(String productId, Pageable pageable) {
        // 1. Kiểm tra product tồn tại
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product không tồn tại với id: " + productId));

        // 2. Query ra Page<ProductPromotion>
        Page<ProductPromotion> ppPage =
                productPromotionRepository.findByProductDetail_Product_ProductId(productId, pageable);

        // 3. Map sang List<Promotion>
        List<Promotion> promotions = ppPage.getContent().stream()
                .map(ProductPromotion::getPromotion)
                .collect(Collectors.toList());

        // 4. Trả về PageImpl<Promotion>
        return new PageImpl<>(promotions, pageable, ppPage.getTotalElements());
    }

    private User checkAdminAndGetCurrentUser() {
        var context = SecurityContextHolder.getContext();
        String userIdLogin = context.getAuthentication().getName();
        System.out.println(userIdLogin);
        return userRepository.findById(userIdLogin)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
    }

    private Promotion mapRequestToPromotion(PromotionRequest req) {
        Promotion promo = new Promotion();
        promo.setPromoCode(req.getPromoCode().trim());
        promo.setDiscountType(req.getDiscountType());
        promo.setDiscountValue(req.getDiscountValue());
        promo.setUsageLimit(req.getUsageLimit());
        promo.setTempUsageLimit(req.getUsageLimit()); // Nếu muốn khởi tạo tempUsageLimit = usageLimit
        promo.setMaxValue(req.getMaxValue());
        promo.setMinOrderValue(req.getMinOrderValue());
        promo.setStartDate(req.getStartDate());
        promo.setEndDate(req.getEndDate());
        return promo;
    }

    @Transactional
    public void createPromotionForAllUsers(Promotion savedPromo) {
        // 1. Lấy tất cả user
        List<User> allUsers = userRepository.findAll();

        // 2. Với mỗi user, tạo UserPromotion
        for (User u : allUsers) {
            UserPromotion up = new UserPromotion();
            up.setPromotion(savedPromo);
            up.setUser(u);
            // Nếu có các trường tracking, set ở đây, ví dụ up.setUsedCount(0); up.setIsActive(true);
            userPromotionRepository.save(up);
            notificationService.notifyPromotion(up);
        }
    }



    @Transactional
    public void createPromotionForSpecificUsers(Promotion savedPromo, List<String> userIds) {
        for (String userId : userIds) {
            // 1. Lấy User
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User không tồn tại với id: " + userId));

            // 2. Kiểm tra mapping đã tồn tại chưa
            boolean alreadyAssigned = userPromotionRepository.existsByUserAndPromotion(user, savedPromo);
            if (alreadyAssigned) {
                continue; // nếu đã gán rồi, bỏ qua
            }

            // 3. Tạo mới UserPromotion
            UserPromotion up = new UserPromotion();
            up.setPromotion(savedPromo);
            up.setUser(user);
            // Nếu cần thêm tracking field (usedCount, isActive...), set tại đây.
            userPromotionRepository.save(up);
        }
    }


    @Override
    public Page<ProductPromotion> getAllProductPromotionPagination(Pageable pageable, String search) {
        Specification<ProductPromotion> spec = ProductPromotionSpecification.filterProductPromotion(search);
        Page<ProductPromotion> productPrmotionPage = productPromotionRepository.findAll(spec, pageable);
        // Có thể thêm logic tính toán thêm ở đây
        List<ProductPromotion> userResponses = productPrmotionPage.getContent().stream().toList();
        return new PageImpl<>(userResponses, pageable, productPrmotionPage.getTotalElements());
    }

    @Override
    @Transactional
    public void createPromotion(PromotionRequest request) {
        // 1. Kiểm tra quyền admin

        // 2. Kiểm tra promoCode có trùng không
        Optional<Promotion> existed = promotionRepository.findByPromoCode(request.getPromoCode());
        if (existed.isPresent()) {
            throw new RuntimeException("PromoCode đã tồn tại, vui lòng chọn mã khác");
        }

        // 3. Map Request -> Promotion entity & lưu
        Promotion promoToSave = mapRequestToPromotion(request);
        Promotion savedPromo = promotionRepository.save(promoToSave);

        // 4. Phân nhánh theo voucherType
        if (request.getVoucherType() == PromotionRequest.VoucherType.ALL_USERS) {
            createPromotionForAllUsers(savedPromo);
        } else if (request.getVoucherType() == PromotionRequest.VoucherType.ALL_PRODUCTS) {
            createPromotionForAllProductDetails(savedPromo);
        } else if (request.getVoucherType() == PromotionRequest.VoucherType.USERS) {
            // 5. branch mới: gán voucher chỉ cho danh sách userId
            List<String> userIds = request.getUserIds();
            if (userIds == null || userIds.isEmpty()) {
                throw new RuntimeException("Phải truyền danh sách userIds khi voucherType == USERS");
            }
            createPromotionForSpecificUsers(savedPromo, userIds);
        } else if (request.getVoucherType() == PromotionRequest.VoucherType.PRODUCTS) {
            // Xử lý gán cho một số product
            List<String> productIds = request.getProductIds();
            if (productIds == null || productIds.isEmpty()) {
                throw new RuntimeException("Phải truyền danh sách productIds khi voucherType == PRODUCTS");
            }
            createPromotionForSpecificProductDetails(savedPromo, productIds);
        } else {
            throw new RuntimeException("VoucherType không hợp lệ cho phương thức này");
        }
    }

    @Transactional
    public void createPromotionForSpecificProductDetails(Promotion savedPromo, List<String> productIds) { // du là productIds nhung thuc chat la productDetailIds
        for (String pdID : productIds){
            ProductDetail productDetail = productDetailRepository.findById(pdID)
                    .orElseThrow(() -> new RuntimeException("ProductDetail không tồn tại với id: " + pdID));

            boolean alreadyAssigned = productPromotionRepository.existsByProductDetailAndPromotion(productDetail, savedPromo);
            if (alreadyAssigned) {
                continue;
            }

            // 3. Tạo và lưu ProductPromotion
            ProductPromotion pp = new ProductPromotion();
            pp.setPromotion(savedPromo);
            pp.setProductDetail(productDetail);

            productPromotionRepository.save(pp);
        }

    }

    @Transactional
    public void createPromotionForAllProductDetails(Promotion savedPromo) {
        List<ProductDetail> allDetails = productDetailRepository.findAll();

        for (ProductDetail detail : allDetails) {
            boolean alreadyAssigned = productPromotionRepository
                    .existsByProductDetailAndPromotion(detail, savedPromo);

            if (alreadyAssigned) {
                continue;
            }

            ProductPromotion pp = new ProductPromotion();
            pp.setPromotion(savedPromo);
            pp.setProductDetail(detail);

            productPromotionRepository.save(pp);
        }
    }

    private void assignPromotionToSpecificUsers(Promotion promo, List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new RuntimeException("Phải truyền danh sách userIds khi voucherType = USERS");
        }

        for (String userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User không tồn tại: " + userId));

            boolean alreadyAssigned = userPromotionRepository.existsByUserAndPromotion(user, promo);
            if (alreadyAssigned) continue;

            UserPromotion up = new UserPromotion();
            up.setPromotion(promo);
            up.setUser(user);
            userPromotionRepository.save(up);

            // Gửi notification sau khi gán promotion cho user
            notificationService.notifyPromotion(up);
        }
    }

}

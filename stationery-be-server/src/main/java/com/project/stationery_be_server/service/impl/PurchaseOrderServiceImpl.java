package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.MomoRequest;
import com.project.stationery_be_server.dto.request.order.PurchaseOrderProductRequest;
import com.project.stationery_be_server.dto.request.order.PurchaseOrderRequest;
import com.project.stationery_be_server.dto.response.AddressResponse;
import com.project.stationery_be_server.dto.response.momo.MomoResponse;
import com.project.stationery_be_server.dto.response.PurchaseOrderDetailResponse;
import com.project.stationery_be_server.dto.response.PurchaseOrderResponse;
import com.project.stationery_be_server.dto.response.product.ProductDetailResponse;
import com.project.stationery_be_server.entity.*;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.repository.*;
import com.project.stationery_be_server.service.NotificationService;
import com.project.stationery_be_server.service.PurchaseOrderService;
import lombok.AccessLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.hc.client5.http.utils.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.stationery_be_server.entity.PurchaseOrder.Status.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    WebClient webClient;
    ProductDetailRepository productDetailRepository;
    CartRepository cartRepository;
    PurchaseOrderRepository purchaseOrderRepository;
    UserRepository userRepository;
    UserPromotionRepository userPromotionRepository;
    ProductPromotionRepository productPromotionRepository;
    AddressRepository addressRepository;
    PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    ProductRepository productRepository;
    PdfGenerationServiceImpl pdfGenerationServiceImpl;
    PaymentRepository paymentRepository;
    private final PromotionRepository promotionRepository;
    InOrderRepository inOrderRepository;
    private final NotificationService notificationService;
    @Value(value = "${momo.partnerCode}")
    @NonFinal
    String partnerCode;
    @Value(value = "${momo.accessKey}")
    @NonFinal
    String accessKey;
    @Value(value = "${momo.secretKey}")
    @NonFinal
    String secretKey;
    @Value(value = "${momo.redirectUrl}")
    @NonFinal
    String redirectUrl;
    @Value(value = "${momo.ipnUrl}")
    @NonFinal
    String ipnUrl;
    @Value(value = "${momo.requestType}")
    @NonFinal
    String requestType;
    @Value(value = "${momo.endpoint}")
    @NonFinal
    String endpoint;
    @Value(value = "${momo.accessKey}")
    @NonFinal
    String accessKeyMomo;
    @Value(value = "${momo.secretKey}")
    @NonFinal
    String secretKeyMomo;
    @Value(value = "${momo.urlCheckTransaction}")
    @NonFinal
    String urlCheckTransaction;

    @Transactional
    public Long handleRequestPurchaseOrder(PurchaseOrderRequest request, String orderId, User user) {
        List<PurchaseOrderDetail> listOderDetail = new ArrayList<>();
        List<PurchaseOrderProductRequest> pdRequest = request.getOrderDetails();
        String userPromotionId = request.getUserPromotionId();


        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setUser(user);
        purchaseOrder.setPurchaseOrderId(orderId);
        purchaseOrder.setStatus(PENDING);
        purchaseOrder.setAddress(addressRepository.findByAddressId(request.getAddressId()).orElseThrow(() -> new AppException(NotExistedErrorCode.ADDRESS_NOT_FOUND)));

        purchaseOrderRepository.save(purchaseOrder);
        Long totalAmount = 0L;
        for (PurchaseOrderProductRequest orderDetail : pdRequest) {
            ProductDetail pd = productDetailRepository.findByProductDetailId(orderDetail.getProductDetailId());
            if (pd == null) {
                throw new RuntimeException("Product detail not found");
            }
            int disCountPrice = pd.getDiscountPrice();
            cartRepository.deleteByUser_UserIdAndProductDetail_ProductDetailId(user.getUserId(), orderDetail.getProductDetailId());
            ProductPromotion promotion = null;
            if (orderDetail.getProductPromotionId() != null) {
                promotion = productPromotionRepository.getValidPromotionForProductDetail(orderDetail.getProductPromotionId(), pd.getDiscountPrice()).orElseThrow(() -> new AppException(NotExistedErrorCode.PRODUCT_PROMOTION_NOT_EXISTED));
                Promotion currentPromotion = promotion.getPromotion();
                currentPromotion.setTempUsageLimit(currentPromotion.getTempUsageLimit() - 1);
                if (currentPromotion.getDiscountType() == Promotion.DiscountType.PERCENTAGE) {
                    // giam %
                    int valueDisCount = (pd.getDiscountPrice() * currentPromotion.getDiscountValue()) / 100;
                    if (currentPromotion.getMaxValue() != null && valueDisCount > currentPromotion.getMaxValue()) { // neu so tien  vuot qua max value
                        disCountPrice -= currentPromotion.getMaxValue();
                    } else {
                        disCountPrice -= valueDisCount;
                    }
                } else {
                    // giam theo gia tri
                    disCountPrice -= currentPromotion.getDiscountValue();
                }
            }

            pd.setAvailableQuantity(pd.getAvailableQuantity() - orderDetail.getQuantity());
            if (pd.getAvailableQuantity() < 0) {
                throw new AppException(NotExistedErrorCode.PRODUCT_NOT_ENOUGH);
            }
            productDetailRepository.save(pd);
            totalAmount += (long) disCountPrice * orderDetail.getQuantity();
            PurchaseOrderDetailId id = new PurchaseOrderDetailId();
            id.setPurchaseOrderId(orderId);  // Chính là orderId được truyền vào
            id.setProductDetailId(pd.getProductDetailId());  // Lấy từ productDetail
            PurchaseOrderDetail purchaseOrderDetail = PurchaseOrderDetail.builder()
                    .purchaseOrderDetailId(id)
                    .quantity(orderDetail.getQuantity())
                    .productPromotion(promotion)
                    .productDetail(pd)
                    .originalPrice(pd.getDiscountPrice())
                    .discountPrice(disCountPrice)
                    .purchaseOrder(purchaseOrder)
                    .build();
            listOderDetail.add(purchaseOrderDetail);

        }
        UserPromotion userPromotion = null;
        if (userPromotionId != null) {
            userPromotion = userPromotionRepository.getValidPromotionForUser(userPromotionId, totalAmount).orElseThrow(() -> new AppException(NotExistedErrorCode.USER_PROMOTION_NOT_FOUND));
            Promotion currentPromotion = userPromotion.getPromotion();
            currentPromotion.setTempUsageLimit(currentPromotion.getTempUsageLimit() - 1);
            if (currentPromotion.getDiscountType() == Promotion.DiscountType.PERCENTAGE) {
                // giam %
                Long valueDisCount = (totalAmount * currentPromotion.getDiscountValue()) / 100;
                if (currentPromotion.getMaxValue() != null && valueDisCount > currentPromotion.getMaxValue()) { // neu so tien  vuot qua max value
                    totalAmount -= currentPromotion.getMaxValue();
                } else {
                    totalAmount -= valueDisCount;
                }
            } else {
                // giam theo gia tri
                totalAmount -= currentPromotion.getDiscountValue();
            }
        }
        purchaseOrder.setPurchaseOrderDetails(listOderDetail);
        purchaseOrder.setUserPromotion(userPromotion);
        purchaseOrder.setAmount(totalAmount);


        purchaseOrderRepository.save(purchaseOrder);
        return totalAmount;
    }

    @Override
    public MomoResponse createOrderWithMomo(PurchaseOrderRequest request) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String orderId = generateOrderId();
        String orderInfo = "Order information " + orderId;
        String requestId = UUID.randomUUID().toString();
        String extraData = "hello ae";
        Long total = handleRequestPurchaseOrder(request, orderId, user);
        String rawSignature = "accessKey=" + accessKey + "&amount=" + total + "&extraData=" + extraData + "&ipnUrl=" + ipnUrl + "&orderId=" + orderId + "&orderInfo=" + orderInfo + "&partnerCode=" + partnerCode + "&redirectUrl=" + redirectUrl + "&requestId=" + requestId + "&requestType=" + requestType;
        String prettySignature = "";

        try {
            prettySignature = generateHmacSHA256(rawSignature, secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (prettySignature.isBlank()) {
            throw new RuntimeException("Signature generation failed");
        }
        MomoRequest requestMomo = MomoRequest.builder()
                .partnerCode(partnerCode)
                .requestType(requestType)
                .redirectUrl(redirectUrl)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .requestId(requestId)
                .amount(total)
                .extraData(extraData)
                .ipnUrl(ipnUrl) // callback khi thanh toan thanh cong
                .signature(prettySignature)
                .lang("vi")
                .build();
        MomoResponse response = webClient
                .post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestMomo)
                .retrieve()
                .bodyToMono(MomoResponse.class)
                .block();
        InOrder inOrder = InOrder.builder()
                .orderId(orderId)
                .user(user)
                .expiredTime(LocalDateTime.now().plusMinutes(2))
                .paymentUrl(response.getPayUrl()) // URL thanh toán, có thể là của MoMo hoặc ZaloPay
                .build();
        inOrderRepository.save(inOrder);

        return response;
    }
    @Override
    public void createOrderNotPayment(PurchaseOrderRequest request) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String orderId = generateOrderId();
        handleRequestPurchaseOrder(request, orderId, user);
    }

    @Override
    @Transactional
    public MomoResponse transactionStatus(String orderId, Integer status) {
        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("Missing input");
        }

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPurchaseOrderId(orderId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.ORDER_NOT_FOUND));
        inOrderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.IN_ORDER_NOT_FOUND));

        String rawSignature = String.format("accessKey=%s&orderId=%s&partnerCode=%s&requestId=%s",
                accessKeyMomo, orderId, partnerCode, orderId);
        String signature = null;
        try {
            signature = generateHmacSHA256(rawSignature, secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        MomoRequest momoRequest = MomoRequest.builder()
                .requestId(orderId)
                .orderId(orderId)
                .partnerCode(partnerCode)
                .lang("vi")
                .signature(signature)
                .build();

        MomoResponse data = webClient.post()
                .uri(urlCheckTransaction)
                .header("Content-Type", "application/json; charset=UTF-8")
                .bodyValue(momoRequest)
                .retrieve()
                .bodyToMono(MomoResponse.class)
                .block();

        if (data == null) throw new RuntimeException("Not paid yet");

        boolean isCanceled = ( data.getResultCode() == 1006 || status == 0);
        if (isCanceled) {
            rollbackOrder(purchaseOrder, orderId);
            return data;
        }

        if (data.getResultCode() == 0) {
            if (paymentRepository.findByPurchaseOrder(purchaseOrder) != null) {
                throw new AppException(NotExistedErrorCode.PAYMENT_EXISTS);
            }
            handleSuccessfulPayment(purchaseOrder, orderId);
        }

        return data;
    }

    private void rollbackOrder(PurchaseOrder purchaseOrder, String orderId) {
        if (purchaseOrder.getUserPromotion() != null) {
            promotionRepository.increaseUsageCountByPromotionId(
                    purchaseOrder.getUserPromotion().getPromotion().getPromotionId());
        }
        List<PurchaseOrderDetail> details = purchaseOrderDetailRepository.findByPurchaseOrder_PurchaseOrderId(orderId);
        for (PurchaseOrderDetail detail : details) {
            String productDetailId = detail.getProductDetail().getProductDetailId();
            productDetailRepository.restoreQuantity(productDetailId, detail.getQuantity());
            productDetailRepository.deleteById(productDetailId);

            if (detail.getProductPromotion() != null) {
                promotionRepository.increaseUsageCountByPromotionId(
                        detail.getProductPromotion().getPromotion().getPromotionId());
            }
        }

        purchaseOrderDetailRepository.deleteAll(details);
        purchaseOrderRepository.deleteById(orderId);
        inOrderRepository.deleteById(orderId);
    }

    private void handleSuccessfulPayment(PurchaseOrder purchaseOrder, String orderId) {
        List<PurchaseOrderDetail> details = purchaseOrderDetailRepository.findByPurchaseOrder_PurchaseOrderId(orderId);
        for (PurchaseOrderDetail detail : details) {
            int result = productDetailRepository.reduceQuantity(
                    detail.getProductDetail().getProductDetailId(), detail.getQuantity());
            if (result == 0) throw new AppException(NotExistedErrorCode.PRODUCT_NOT_ENOUGH);

            if (detail.getProductPromotion() != null) {
                promotionRepository.reduceUsageCountByPromotionId(
                        detail.getProductPromotion().getPromotion().getPromotionId());
            }
        }

        if (purchaseOrder.getUserPromotion() != null) {
            promotionRepository.reduceUsageCountByPromotionId(
                    purchaseOrder.getUserPromotion().getPromotion().getPromotionId());
        }

        Payment payment = Payment.builder()
                .status(1)
                .payType("qr")
                .payName("momo")
                .purchaseOrder(purchaseOrder)
                .build();

        purchaseOrder.setStatus(PurchaseOrder.Status.COMPLETED);
        inOrderRepository.deleteById(orderId);
        paymentRepository.save(payment);
        purchaseOrderRepository.save(purchaseOrder);
    }

    public String generateHmacSHA256(String data, String key) throws Exception {

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }

    public String generateOrderId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    // ***USER: Lấy all đơn hàng
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getUserOrdersByStatus(String userId, String status) {
        List<PurchaseOrder> orders;

        // Kiểm tra nếu status là 'all' thì lấy tất cả đơn hàng theo userId
        if ("all".equalsIgnoreCase(status)) {
            orders = purchaseOrderRepository.findByUser_UserIdWithDetails(userId);
        } else {
            PurchaseOrder.Status orderStatus;
            try {
                orderStatus = PurchaseOrder.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AppException(NotExistedErrorCode.INVALID_STATUS);
            }
            orders = purchaseOrderRepository.findByUser_UserIdAndStatusWithDetails(userId, orderStatus);
        }

        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        return orders.stream()
                .map(order -> PurchaseOrderResponse.builder()
                        .purchaseOrderId(order.getPurchaseOrderId())
                        .createdAt(order.getCreatedAt() != null
                                ? java.util.Date.from(order.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant())
                                : null)
                        .expiredTime(order.getExpiredTime() != null
                                ? LocalDateTime.parse(order.getExpiredTime().toString())
                                : null)
                        .pdfUrl(order.getPdfUrl())
                        .userPromotionId(order.getUserPromotion() != null ? order.getUserPromotion().getUserPromotionId() : null)
                        .status(order.getStatus())
                        .note(order.getNote())
                        .cancelReason(order.getCancelReason())
                        .amount(BigDecimal.valueOf(order.getAmount()))
                        .orderDetails(order.getPurchaseOrderDetails().stream()
                                .map(detail -> PurchaseOrderDetailResponse.builder()
                                        .productDetailId(detail.getPurchaseOrderDetailId().getProductDetailId())
                                        .quantity(detail.getQuantity())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }


    @Override
    public List<ProductDetailResponse> getProductDetailsByOrderId(String purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPurchaseOrderId(purchaseOrderId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.PRODUCT_NOT_EXISTED));

        List<PurchaseOrderDetail> orderDetails = purchaseOrderDetailRepository.findByPurchaseOrder(purchaseOrder);
        return orderDetails.stream()
                .map(orderDetail -> {
                    var productDetail = orderDetail.getProductDetail();
                    if (productDetail == null) {
                        throw new AppException(NotExistedErrorCode.PRODUCT_NOT_EXISTED);
                    }

                    List<Image> images;
                    String colorId = productDetail.getColor() != null ? productDetail.getColor().getColorId(): null;

                    List<Image> allImages = productDetail.getProduct().getImages();

                    if (colorId == null) {
                        // Nếu không có colorId, lấy tất cả ảnh thuộc product
                        images = allImages != null ? allImages : List.of();
                    } else {
                        // Nếu có colorId, lọc ảnh theo productId và colorId
                        images = allImages != null
                                ? allImages.stream()
                                .filter(img -> img.getColor() != null && colorId.equals(img.getColor().getColorId()))
                                .collect(Collectors.toList())
                                : List.of();
                    }

                    return ProductDetailResponse.builder()
                            .productDetailId(productDetail.getProductDetailId())
                            .slug(productDetail.getSlug() != null ? productDetail.getSlug() : "")
                            .name(productDetail.getName() != null ? productDetail.getName() : "Unknown")
                            .stockQuantity(productDetail.getStockQuantity() != 0 ? productDetail.getStockQuantity() : 0)
                            .soldQuantity(productDetail.getSoldQuantity() != 0 ? productDetail.getSoldQuantity() : 0)
                            .originalPrice(productDetail.getOriginalPrice() != 0 ? productDetail.getOriginalPrice() : 0)
                            .discountPrice(orderDetail.getDiscountPrice() != null ? orderDetail.getDiscountPrice() : 0)
                            .size(productDetail.getSize())
                            .color(productDetail.getColor())
                            .createdAt(productDetail.getCreatedAt())
                            .images(images)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Hàm mới để lấy tất cả thông tin chi tiết đơn hàng
    public PurchaseOrderResponse getPurchaseOrderDetails(String purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPurchaseOrderId(purchaseOrderId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.PURCHASE_ORDER_NOT_EXISTED)); // Đổi errorCode cho phù hợp

        // Lấy thông tin Address
        Address address = purchaseOrder.getAddress(); // Giả sử PurchaseOrder có trường 'address' là một đối tượng Address
        AddressResponse addressResponse = null;
        if (address != null) {
            addressResponse = AddressResponse.builder()
                    .addressId(address.getAddressId())
                    .addressName(address.getAddressName())
                    .recipient(address.getRecipient())
                    .phone(address.getPhone())
                    .build();
        }

        // Lấy thông tin Payment
/*        Payment payment = purchaseOrder.getPayment(); // Giả sử PurchaseOrder có trường 'payment' là một đối tượng Payment
        PaymentResponse paymentResponse = null;
        if (payment != null) {
            paymentResponse = PaymentResponse.builder()
                    .paymentId(payment.getPaymentId())
                    .createdAt(payment.getCreatedAt())
                    .payName(payment.getPayName())
                    .payType(payment.getPayType())
                    .status(payment.getStatus())
                    .build();
        }*/

        // Lấy thông tin Product Details (phần mã đã có của bạn)
        List<PurchaseOrderDetail> orderDetails = purchaseOrderDetailRepository.findByPurchaseOrder(purchaseOrder);
        List<ProductDetailResponse> productDetails = orderDetails.stream()
                .map(orderDetail -> {
                    var productDetail = orderDetail.getProductDetail();
                    if (productDetail == null) {
                        throw new AppException(NotExistedErrorCode.PRODUCT_NOT_EXISTED);
                    }

                    List<Image> images;
                    String colorId = productDetail.getColor() != null ? productDetail.getColor().getColorId(): null;

                    List<Image> allImages = productDetail.getProduct().getImages();

                    if (colorId == null) {
                        images = allImages != null ? allImages : List.of();
                    } else {
                        images = allImages != null
                                ? allImages.stream()
                                .filter(img -> img.getColor() != null && colorId.equals(img.getColor().getColorId()))
                                .collect(Collectors.toList())
                                : List.of();
                    }

                    return ProductDetailResponse.builder()
                            .productDetailId(productDetail.getProductDetailId())
                            .slug(productDetail.getSlug() != null ? productDetail.getSlug() : "")
                            .name(productDetail.getName() != null ? productDetail.getName() : "Unknown")
                            .stockQuantity(productDetail.getStockQuantity() != 0 ? productDetail.getStockQuantity() : 0)
                            .soldQuantity(productDetail.

                                    getSoldQuantity() != 0 ? productDetail.getSoldQuantity() : 0)
                            .originalPrice(productDetail.getOriginalPrice() != 0 ? productDetail.getOriginalPrice() : 0)
                            .discountPrice(orderDetail.getDiscountPrice() != null ? orderDetail.getDiscountPrice() : 0)
                            .size(productDetail.getSize())
                            .color(productDetail.getColor())
                            .createdAt(productDetail.getCreatedAt())
                            .images(images)
                            .build();
                })
                .collect(Collectors.toList());

        List<PurchaseOrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(detail -> PurchaseOrderDetailResponse.builder()
                        .productDetailId(detail.getProductDetail().getProductDetailId())
                        .quantity(detail.getQuantity())
                        .build())
                .collect(Collectors.toList());


        // Xây dựng đối tượng phản hồi cuối cùng
        return PurchaseOrderResponse.builder()
                .purchaseOrderId(purchaseOrder.getPurchaseOrderId())
                .status(purchaseOrder.getStatus())
                .note(purchaseOrder.getNote())
                .amount(BigDecimal.valueOf(purchaseOrder.getAmount()))
                .pdfUrl(purchaseOrder.getPdfUrl())
                .createdAt(purchaseOrder.getCreatedAt() != null
                        ? java.util.Date.from(purchaseOrder.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant())
                        : null)
                .expiredTime(purchaseOrder.getExpiredTime())
                .cancelReason(purchaseOrder.getCancelReason())
                .address(addressResponse)
                //.payment(paymentResponse)
                .productDetails(productDetails)
                .orderDetails(orderDetailResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<PurchaseOrder.Status, Long> getOrderStatusStatistics(String userId) {
        // Kiểm tra user tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));

        // Lấy danh sách đơn hàng theo userId
        List<PurchaseOrder> orders = purchaseOrderRepository.findByUser_UserIdWithDetails(userId);

        // Thống kê số lượng đơn hàng theo từng trạng thái
        Map<PurchaseOrder.Status, Long> statistics = orders.stream()
                .collect(Collectors.groupingBy(
                        PurchaseOrder::getStatus,
                        Collectors.counting()
                ));

        // Đảm bảo tất cả trạng thái đều có trong map, kể cả khi số lượng là 0
        Arrays.stream(PurchaseOrder.Status.values())
                .forEach(status -> statistics.putIfAbsent(status, 0L));

        return statistics;
    }

    //Cancel Order
    @Override
    @Transactional
    public void cancelOrder(String userId, String purchaseOrderId, String cancelReason) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));

        // Find the order
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPurchaseOrderId(purchaseOrderId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.ORDER_NOT_FOUND));

        // Verify the order belongs to the user
        if (!purchaseOrder.getUser().getUserId().equals(userId)) {
            throw new AppException(NotExistedErrorCode.ORDER_NOT_FOUND);
        }

        // Check if the order is in a cancellable state
        if (purchaseOrder.getStatus() != PENDING && purchaseOrder.getStatus() != PROCESSING) {
            throw new AppException(NotExistedErrorCode.ORDER_NOT_CANCELLABLE);
        }

        // Restore product quantities
        List<PurchaseOrderDetail> orderDetails = purchaseOrderDetailRepository.findByPurchaseOrder_PurchaseOrderId(purchaseOrderId);
        for (PurchaseOrderDetail detail : orderDetails) {
            ProductDetail productDetail = detail.getProductDetail();
            productDetail.setAvailableQuantity(productDetail.getAvailableQuantity() + detail.getQuantity());
            productDetailRepository.save(productDetail);

            // Restore product promotion usage limit if applicable
            if (detail.getProductPromotion() != null) {
                Promotion promotion = detail.getProductPromotion().getPromotion();
                promotion.setTempUsageLimit(promotion.getTempUsageLimit() + 1);
                promotionRepository.save(promotion);
            }
        }

        // Restore user promotion usage limit if applicable
        if (purchaseOrder.getUserPromotion() != null) {
            Promotion userPromotion = purchaseOrder.getUserPromotion().getPromotion();
            userPromotion.setTempUsageLimit(userPromotion.getTempUsageLimit() + 1);
            promotionRepository.save(userPromotion);
        }

        // Update order status to CANCELED
        purchaseOrder.setStatus(CANCELED);
        purchaseOrder.setCancelReason(cancelReason);
        purchaseOrder.setExpiredTime(null);
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse editPurchaseOrder(String userId, String purchaseOrderId, PurchaseOrderRequest request) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));

        // Find the order
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPurchaseOrderId(purchaseOrderId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.ORDER_NOT_FOUND));

        // Verify the order belongs to the user
        if (!purchaseOrder.getUser().getUserId().equals(userId)) {
            throw new AppException(NotExistedErrorCode.ORDER_NOT_FOUND);
        }

        // Check if order is in PENDING status
        if (purchaseOrder.getStatus() != PENDING) {
            throw new AppException(NotExistedErrorCode.ORDER_NOT_EDITABLE);
        }

        // Update address if provided
        if (request.getAddressId() != null) {
            Address address = addressRepository.findByAddressId(request.getAddressId())
                    .orElseThrow(() -> new AppException(NotExistedErrorCode.ADDRESS_NOT_FOUND));
            purchaseOrder.setAddress(address);
        }

        // Update note if provided
        if (request.getNote() != null) {
            purchaseOrder.setNote(request.getNote());
        }

        // Update user promotion if provided
        if (request.getUserPromotionId() != null) {
            // If there's an existing user promotion, restore its usage limit
            if (purchaseOrder.getUserPromotion() != null) {
                Promotion currentPromotion = purchaseOrder.getUserPromotion().getPromotion();
                currentPromotion.setTempUsageLimit(currentPromotion.getTempUsageLimit() + 1);
                promotionRepository.save(currentPromotion);
            }

            // Apply new user promotion
            UserPromotion newUserPromotion = userPromotionRepository.getValidPromotionForUser(
                    request.getUserPromotionId(),
                    purchaseOrder.getAmount()
            ).orElseThrow(() -> new AppException(NotExistedErrorCode.USER_PROMOTION_NOT_FOUND));

            Promotion newPromotion = newUserPromotion.getPromotion();
            newPromotion.setTempUsageLimit(newPromotion.getTempUsageLimit() - 1);
            promotionRepository.save(newPromotion);
            purchaseOrder.setUserPromotion(newUserPromotion);

            // Recalculate total amount with new promotion
            Long totalAmount = recalculateTotalAmount(purchaseOrder);
            purchaseOrder.setAmount(totalAmount);
        }

        // Save updated purchase order
        purchaseOrderRepository.save(purchaseOrder);

        // Return response
        return PurchaseOrderResponse.builder()
                .purchaseOrderId(purchaseOrder.getPurchaseOrderId())
                .createdAt(purchaseOrder.getCreatedAt() != null
                        ? java.util.Date.from(purchaseOrder.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant())
                        : null)
                .expiredTime(purchaseOrder.getExpiredTime() != null
                        ? LocalDateTime.parse(purchaseOrder.getExpiredTime().toString())
                        : null)
                .pdfUrl(purchaseOrder.getPdfUrl())
                .userPromotionId(purchaseOrder.getUserPromotion() != null
                        ? purchaseOrder.getUserPromotion().getUserPromotionId()
                        : null)
                .status(purchaseOrder.getStatus())
                .note(purchaseOrder.getNote())
                .amount(BigDecimal.valueOf(purchaseOrder.getAmount()))
                .orderDetails(purchaseOrder.getPurchaseOrderDetails().stream()
                        .map(detail -> PurchaseOrderDetailResponse.builder()
                                .productDetailId(detail.getPurchaseOrderDetailId().getProductDetailId())
                                .quantity(detail.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // Helper method to recalculate total amount with new promotion
    private Long recalculateTotalAmount(PurchaseOrder purchaseOrder) {
        Long totalAmount = 0L;
        for (PurchaseOrderDetail detail : purchaseOrder.getPurchaseOrderDetails()) {
            totalAmount += detail.getDiscountPrice();
        }

        if (purchaseOrder.getUserPromotion() != null) {
            Promotion promotion = purchaseOrder.getUserPromotion().getPromotion();
            if (promotion.getDiscountType() == Promotion.DiscountType.PERCENTAGE) {
                Long valueDiscount = (totalAmount * promotion.getDiscountValue()) / 100;
                if (promotion.getMaxValue() != null && valueDiscount > promotion.getMaxValue()) {
                    totalAmount -= promotion.getMaxValue();
                } else {
                    totalAmount -= valueDiscount;
                }
            } else {
                totalAmount -= promotion.getDiscountValue();
            }
        }
        return totalAmount;
    }

    //Order ADMIN
    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponse> getAllPendingOrders(String roleName, Pageable pageable) {
        Page<PurchaseOrder> orders = purchaseOrderRepository.findPendingOrdersByFilters(
                roleName, PurchaseOrder.Status.PENDING, pageable);
        if (orders.isEmpty()) {
            return Page.empty(pageable);
        }
        return orders.map(order -> PurchaseOrderResponse.builder()
                .purchaseOrderId(order.getPurchaseOrderId())
                .createdAt(order.getCreatedAt() != null
                        ? java.util.Date.from(order.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant())
                        : null)
                .pdfUrl(order.getPdfUrl())
                .userPromotionId(order.getUserPromotion() != null ? order.getUserPromotion().getUserPromotionId() : null)
                .status(order.getStatus())
                .amount(BigDecimal.valueOf(order.getAmount()))
                .note(order.getNote())
                .cancelReason(order.getCancelReason())
                .orderDetails(order.getPurchaseOrderDetails().stream()
                        .map(detail -> PurchaseOrderDetailResponse.builder()
                                .productDetailId(detail.getPurchaseOrderDetailId().getProductDetailId())
                                .quantity(detail.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .userId(order.getUser().getUserId())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponse> getAllNonPendingOrders(String roleName, List<PurchaseOrder.Status> status, Pageable pageable) {
        List<PurchaseOrder.Status> statuses = (status == null || status.isEmpty())
                ? List.of(PurchaseOrder.Status.PROCESSING, PurchaseOrder.Status.SHIPPING,
                PurchaseOrder.Status.COMPLETED, PurchaseOrder.Status.CANCELED)
                : status;
        Page<PurchaseOrder> orders = purchaseOrderRepository.findNonPendingOrdersByFilters(roleName, statuses, pageable);
        if (orders.isEmpty()) {
            return Page.empty(pageable);
        }
        return orders.map(order -> PurchaseOrderResponse.builder()
                .purchaseOrderId(order.getPurchaseOrderId())
                .createdAt(order.getCreatedAt() != null
                        ? java.util.Date.from(order.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant())
                        : null)
                .expiredTime(order.getExpiredTime())
                .pdfUrl(order.getPdfUrl())
                .userPromotionId(order.getUserPromotion() != null ? order.getUserPromotion().getUserPromotionId() : null)
                .status(order.getStatus())
                .note(order.getNote())
                .cancelReason(order.getCancelReason())
                .amount(BigDecimal.valueOf(order.getAmount()))
                .orderDetails(order.getPurchaseOrderDetails().stream()
                        .map(detail -> PurchaseOrderDetailResponse.builder()
                                .productDetailId(detail.getPurchaseOrderDetailId().getProductDetailId())
                                .quantity(detail.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .userId(order.getUser().getUserId())
                .build());
    }

    @Override
    @Transactional
    public void confirmOrder(String purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPurchaseOrderId(purchaseOrderId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.ORDER_NOT_FOUND));

        if (purchaseOrder.getStatus() != PurchaseOrder.Status.PENDING) {
            throw new AppException(NotExistedErrorCode.ORDER_NOT_PENDING);
        }

        purchaseOrder.setStatus(PurchaseOrder.Status.PROCESSING);
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    @Transactional
    public void updateOrderStatus(String userId, String purchaseOrderId, String statusStr, String cancelReason) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        System.out.println("[DEBUG]: purchaseOrderId" + purchaseOrderId);
        // Find the order
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPurchaseOrderId(purchaseOrderId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.ORDER_NOT_FOUND));

        // Convert status string to enum
        PurchaseOrder.Status status;
        try {
            status = PurchaseOrder.Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(NotExistedErrorCode.ORDER_NOT_FOUND);
        }

        // Check if the order is in a valid state for updating
        if (purchaseOrder.getStatus() == PurchaseOrder.Status.PENDING &&
                status != PurchaseOrder.Status.PROCESSING &&
                status != PurchaseOrder.Status.CANCELED) {
            throw new AppException(NotExistedErrorCode.ORDER_STILL_PENDING);
        }

        // If updating to CANCELED, check if the order is cancellable and restore quantities/promotions
        if (status == PurchaseOrder.Status.CANCELED) {
            if (purchaseOrder.getStatus() != PurchaseOrder.Status.PENDING &&
                    purchaseOrder.getStatus() != PurchaseOrder.Status.PROCESSING) {
                throw new AppException(NotExistedErrorCode.ORDER_NOT_CANCELLABLE);
            }

            // Restore product quantities
            List<PurchaseOrderDetail> orderDetails = purchaseOrderDetailRepository.findByPurchaseOrder_PurchaseOrderId(purchaseOrderId);
            for (PurchaseOrderDetail detail : orderDetails) {
                ProductDetail productDetail = detail.getProductDetail();
                productDetail.setAvailableQuantity(productDetail.getAvailableQuantity() + detail.getQuantity());
                productDetailRepository.save(productDetail);

                // Restore product promotion usage limit if applicable
                if (detail.getProductPromotion() != null) {
                    Promotion promotion = detail.getProductPromotion().getPromotion();
                    promotion.setTempUsageLimit(promotion.getTempUsageLimit() + 1);
                    promotionRepository.save(promotion);
                }
            }

            // Restore user promotion usage limit if applicable
            if (purchaseOrder.getUserPromotion() != null) {
                Promotion userPromotion = purchaseOrder.getUserPromotion().getPromotion();
                userPromotion.setTempUsageLimit(userPromotion.getTempUsageLimit() + 1);
                promotionRepository.save(userPromotion);
            }

            // Set cancel reason and clear expired time
            purchaseOrder.setCancelReason(cancelReason != null ? cancelReason : "Hủy bởi quản trị viên");
            purchaseOrder.setExpiredTime(null);
        }

        // Update order status
        purchaseOrder.setStatus(status);
        purchaseOrderRepository.save(purchaseOrder);
        notificationService.notifyOrderUpdate(purchaseOrder);
    }
}
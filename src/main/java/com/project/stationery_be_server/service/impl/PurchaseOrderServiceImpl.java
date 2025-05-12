package com.project.stationery_be_server.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.MomoRequest;
import com.project.stationery_be_server.dto.request.order.PurchaseOrderProductRequest;
import com.project.stationery_be_server.dto.request.order.PurchaseOrderRequest;
import com.project.stationery_be_server.dto.response.momo.MomoResponse;
import com.project.stationery_be_server.dto.response.PurchaseOrderDetailResponse;
import com.project.stationery_be_server.dto.response.PurchaseOrderResponse;
import com.project.stationery_be_server.entity.*;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.repository.*;
import com.project.stationery_be_server.service.PurchaseOrderService;
import lombok.AccessLevel;
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

import static com.project.stationery_be_server.entity.PurchaseOrder.Status.PENDING;

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
    PaymentRepository paymentRepository;
    private final PromotionRepository promotionRepository;
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
    public Long handleRequestPurchaseOrder(PurchaseOrderRequest request, String orderId) {
        List<PurchaseOrderDetail> listOderDetail = new ArrayList<>();
        List<PurchaseOrderProductRequest> pdRequest = request.getOrderDetails();
        String userPromotionId = request.getUserPromotionId();

        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setUser(user);
        purchaseOrder.setPurchaseOrderId(orderId);
        purchaseOrder.setStatus(PENDING);
        purchaseOrder.setAddress(addressRepository.findByAddressId(request.getAddressId()).orElseThrow(() -> new AppException(NotExistedErrorCode.ADDRESS_NOT_FOUND)));
        purchaseOrder.setExpiredTime(LocalDateTime.now().plusMinutes(9));

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
                    if (currentPromotion.getMaxValue()!= null && valueDisCount > currentPromotion.getMaxValue()) { // neu so tien  vuot qua max value
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
            if(pd.getAvailableQuantity() < 0){
                throw new AppException(NotExistedErrorCode.PRODUCT_NOT_ENOUGH);
            }
            productDetailRepository.save(pd);
            totalAmount += disCountPrice;
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
                if (currentPromotion.getMaxValue()!= null && valueDisCount > currentPromotion.getMaxValue()) { // neu so tien  vuot qua max value
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
    @Transactional
    public MomoResponse createOrderWithMomo(PurchaseOrderRequest request) {

        String orderId = generateOrderId();
        String orderInfo = "Order information " + orderId;
        String requestId = UUID.randomUUID().toString();
        String extraData = "hello ae";
        Long total = handleRequestPurchaseOrder(request,orderId);
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
        return webClient
                .post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestMomo)
                .retrieve()
                .bodyToMono(MomoResponse.class)
                .block();
    }

    @Override
    @Transactional
    public MomoResponse transactionStatus(String orderId) {

        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("Missing input");
        }
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPurchaseOrderId(orderId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.ORDER_NOT_FOUND));
        String rawSignature = String.format(
                "accessKey=%s&orderId=%s&partnerCode=%s&requestId=%s",
                accessKeyMomo, orderId, partnerCode, orderId
        );
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
        if (data == null) {
            throw new RuntimeException("Not paid yet");
        }
        Payment currentPayment = paymentRepository.findByPurchaseOrder(purchaseOrder);
        if(currentPayment != null){
            throw new AppException(NotExistedErrorCode.PAYMENT_EXISTS);
        }
        if (data.getResultCode() == 0) {
            Payment payment = Payment.builder()
                    .status(1)
                    .payType("qr")
                    .payName("momo")
                    .purchaseOrder(purchaseOrder)
                    .build();

            if(purchaseOrder.getUserPromotion() != null){
                Promotion currentPromotion = purchaseOrder.getUserPromotion().getPromotion();
                promotionRepository.reduceUsageCountByPromotionId(currentPromotion.getPromotionId());
            }
            List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailRepository.findByPurchaseOrder_PurchaseOrderId(orderId);
            for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails) {
                int res = productDetailRepository.reduceQuantity(purchaseOrderDetail.getProductDetail().getProductDetailId(), purchaseOrderDetail.getQuantity());
                if(res == 0){
                    throw new AppException(NotExistedErrorCode.PRODUCT_NOT_ENOUGH);
                }
                if(purchaseOrderDetail.getProductPromotion()!= null){
                    promotionRepository.reduceUsageCountByPromotionId(purchaseOrderDetail.getProductPromotion().getPromotion().getPromotionId());
                }
            }
            purchaseOrder.setStatus(PurchaseOrder.Status.COMPLETED);
            purchaseOrder.setExpiredTime(null);
            paymentRepository.save(payment);
            purchaseOrderRepository.save(purchaseOrder);
        }
        return data;
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

}
package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.MomoRequest;
import com.project.stationery_be_server.dto.response.InvoiceResponse;
import com.project.stationery_be_server.dto.response.momo.MomoResponse;
import com.project.stationery_be_server.dto.response.MonthlyInvoiceSummaryResponse;
import com.project.stationery_be_server.entity.*;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.repository.*;
import com.project.stationery_be_server.service.PdfGenerationService;
import com.project.stationery_be_server.service.DepartmentInvoiceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.hc.client5.http.utils.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepartmentInvoiceServiceImpl implements DepartmentInvoiceService {
    WebClient webClient;
    PurchaseOrderRepository purchaseOrderRepository;
    UserRepository userRepository;
    PdfGenerationService pdfGenerationService;
    PaymentRepository paymentRepository;
    ProductDetailRepository productDetailRepository;
    PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    PromotionRepository promotionRepository;
    InOrderRepository inOrderRepository;

    @Value("${momo.partnerCode}")
    @NonFinal String partnerCode;
    @Value("${momo.accessKey}")
    @NonFinal String accessKey;
    @Value("${momo.secretKey}")
    @NonFinal String secretKey;
    @Value("${momo.redirectUrl}")
    @NonFinal String redirectUrl;
    @Value("${momo.ipnUrl}")
    @NonFinal String ipnUrl;
    @Value("${momo.requestType}")
    @NonFinal String requestType;
    @Value("${momo.endpoint}")
    @NonFinal String endpoint;
    @Value("${momo.urlCheckTransaction}")
    @NonFinal String urlCheckTransaction;

    @Transactional(readOnly = true)
    @Override
    public MonthlyInvoiceSummaryResponse getCurrentMonthInvoiceSummary(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        if (!"113".equals(user.getRole().getRoleId())) {
            throw new AppException(NotExistedErrorCode.USER_EXISTED);
        }

        LocalDateTime now = LocalDateTime.now();
        List<PurchaseOrder.Status> unpaidStatuses = List.of(PurchaseOrder.Status.PROCESSING);
        List<PurchaseOrder> unpaidOrders = purchaseOrderRepository
                .findByUser_UserIdAndCreatedAtLessThanEqualAndStatusIn(userId, now, unpaidStatuses);

        BigDecimal totalUnpaidAmount = unpaidOrders.stream()
                .map(order -> BigDecimal.valueOf(order.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int unpaidCount = unpaidOrders.size();

        return MonthlyInvoiceSummaryResponse.builder()
                .userId(userId)
                .month(now.getMonthValue())
                .year(now.getYear())
                .totalAmount(totalUnpaidAmount)
                .orderCount(unpaidCount)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<InvoiceResponse> getAllInvoices(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        if (!"113".equals(user.getRole().getRoleId())) {
            throw new AppException(NotExistedErrorCode.USER_EXISTED);
        }

        List<PurchaseOrder> completedOrders = purchaseOrderRepository.findByUser_UserIdAndStatus(userId, PurchaseOrder.Status.COMPLETED);
        return completedOrders.stream()
                .map(order -> InvoiceResponse.builder()
                        .purchaseOrderId(order.getPurchaseOrderId())
                        .amount(BigDecimal.valueOf(order.getAmount()))
                        .createdAt(order.getCreatedAt())
                        .pdfUrl(order.getPdfUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public String generateCurrentInvoicePdf(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        if (!user.getRole().getRoleId().equals("113")) {
            throw new AppException(NotExistedErrorCode.USER_EXISTED);
        }

        PurchaseOrder lastInvoice = purchaseOrderRepository.findTopByUser_UserIdAndNoteContainingOrderByCreatedAtDesc(
                userId, "Monthly invoice");
        LocalDateTime startDate = lastInvoice != null ? lastInvoice.getCreatedAt() : LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now();

        String pdfUrl = pdfGenerationService.generateAndUploadCurrentInvoicePdf(userId, startDate, endDate);
        PurchaseOrder summaryOrder = PurchaseOrder.builder()
                .purchaseOrderId(UUID.randomUUID().toString().replace("-", "").toUpperCase())
                .user(user)
                .status(PurchaseOrder.Status.COMPLETED)
                .amount(getCurrentMonthInvoiceSummary(userId).getTotalAmount().longValue())
                .createdAt(LocalDateTime.now())
                .note("Monthly invoice from " + startDate.toLocalDate() + " to " + endDate.toLocalDate())
                .pdfUrl(pdfUrl)
                .purchaseOrderDetails(new ArrayList<>())
                .build();

        purchaseOrderRepository.save(summaryOrder);
        return pdfUrl;
    }

    @Transactional
    @Override
    public MomoResponse payCurrentInvoice(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        if (!user.getRole().getRoleId().equals("113")) {
            throw new AppException(NotExistedErrorCode.USER_EXISTED);
        }

        PurchaseOrder lastInvoice = purchaseOrderRepository.findTopByUser_UserIdAndNoteContainingOrderByCreatedAtDesc(
                userId, "Monthly invoice");
        LocalDateTime startDate = lastInvoice != null ? lastInvoice.getCreatedAt() : LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now();

        List<PurchaseOrder> orders = purchaseOrderRepository.findByUser_UserIdAndCreatedAtBetween(
                userId, startDate, endDate);
        if (orders.isEmpty()) {
            throw new AppException(NotExistedErrorCode.ORDER_NOT_FOUND);
        }

        long totalAmount = 0;
        List<PurchaseOrder> unpaidOrders = new ArrayList<>();
        for (PurchaseOrder order : orders) {
            if (order.getStatus() == PurchaseOrder.Status.PROCESSING) {
                List<PurchaseOrderDetail> details = order.getPurchaseOrderDetails();
                if (!details.isEmpty()) {
                    totalAmount += order.getAmount();
                    unpaidOrders.add(order);
                }
            }
        }

        if (unpaidOrders.isEmpty() || totalAmount <= 0) {
            throw new AppException(NotExistedErrorCode.ORDER_NOT_PAY);
        }

        String orderId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        PurchaseOrder monthlyOrder = PurchaseOrder.builder()
                .purchaseOrderId(orderId)
                .user(user)
                .status(PurchaseOrder.Status.PENDING) // Set to PENDING until payment is confirmed
                .amount(totalAmount)
                .createdAt(LocalDateTime.now())
                .note("Monthly payment from " + startDate.toLocalDate() + " to " + endDate.toLocalDate())
                .purchaseOrderDetails(new ArrayList<>())
                .build();

        purchaseOrderRepository.save(monthlyOrder);

        String orderInfo = "Monthly payment from " + startDate.toLocalDate() + " to " + endDate.toLocalDate() + " - Department: " + userId;
        String requestId = UUID.randomUUID().toString();
        String extraData = "monthly_payment";
        String rawSignature = "accessKey=" + accessKey + "&amount=" + totalAmount + "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl + "&orderId=" + orderId + "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode + "&redirectUrl=" + redirectUrl + "&requestId=" + requestId +
                "&requestType=" + requestType;

        String signature;
        try {
            signature = generateHmacSignature(rawSignature, secretKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature: " + e.getMessage());
        }

        MomoRequest requestMomo = MomoRequest.builder()
                .partnerCode(partnerCode)
                .requestType(requestType)
                .redirectUrl(redirectUrl)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .requestId(requestId)
                .amount(totalAmount)
                .extraData(extraData)
                .ipnUrl(ipnUrl)
                .signature(signature)
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
                .paymentUrl(response.getPayUrl())
                .build();
        inOrderRepository.save(inOrder);

        monthlyOrder.setNote(monthlyOrder.getNote() + " | MoMo Request ID: " + requestId);
        purchaseOrderRepository.save(monthlyOrder);

        return response;
    }

    @Transactional
    public MomoResponse transactionStatus(String orderId, Integer status) {
        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("Missing input");
        }

        PurchaseOrder monthlyOrder = purchaseOrderRepository.findByPurchaseOrderId(orderId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.ORDER_NOT_FOUND));
        InOrder inOrder = inOrderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.IN_ORDER_NOT_FOUND));

        String rawSignature = String.format("accessKey=%s&orderId=%s&partnerCode=%s&requestId=%s",
                accessKey, orderId, partnerCode, orderId);
        String signature;
        try {
            signature = generateHmacSignature(rawSignature, secretKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature: " + e.getMessage());
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

        boolean isCanceled = (data.getResultCode() == 1006 || status == 0);
        if (isCanceled) {
            rollbackOrder(monthlyOrder, orderId);
            return data;
        }

        if (data.getResultCode() == 0) {
            if (paymentRepository.findByPurchaseOrder(monthlyOrder) != null) {
                throw new AppException(NotExistedErrorCode.PAYMENT_EXISTS);
            }
            handleSuccessfulPayment(monthlyOrder, orderId);
        }

        return data;
    }

    private void rollbackOrder(PurchaseOrder monthlyOrder, String orderId) {
        // Roll back monthly order (no inventory/promotions to restore since it's a summary order)
        purchaseOrderRepository.deleteById(orderId);
        inOrderRepository.deleteById(orderId);
    }

    private void handleSuccessfulPayment(PurchaseOrder monthlyOrder, String orderId) {
        // Find all unpaid orders covered by this monthly payment
        LocalDateTime startDate = LocalDateTime.parse(monthlyOrder.getNote().split(" ")[3]);
        LocalDateTime endDate = LocalDateTime.parse(monthlyOrder.getNote().split(" ")[5]);
        List<PurchaseOrder> unpaidOrders = purchaseOrderRepository.findByUser_UserIdAndCreatedAtBetween(
                        monthlyOrder.getUser().getUserId(), startDate, endDate).stream()
                .filter(order -> order.getStatus() == PurchaseOrder.Status.PROCESSING)
                .collect(Collectors.toList());

        // Update each unpaid order
        for (PurchaseOrder order : unpaidOrders) {
            List<PurchaseOrderDetail> details = purchaseOrderDetailRepository.findByPurchaseOrder_PurchaseOrderId(
                    order.getPurchaseOrderId());
            for (PurchaseOrderDetail detail : details) {
                int result = productDetailRepository.reduceQuantity(
                        detail.getProductDetail().getProductDetailId(), detail.getQuantity());
                if (result == 0) {
                    throw new AppException(NotExistedErrorCode.PRODUCT_NOT_ENOUGH);
                }
                if (detail.getProductPromotion() != null) {
                    promotionRepository.reduceUsageCountByPromotionId(
                            detail.getProductPromotion().getPromotion().getPromotionId());
                }
            }
            if (order.getUserPromotion() != null) {
                promotionRepository.reduceUsageCountByPromotionId(
                        order.getUserPromotion().getPromotion().getPromotionId());
            }
            order.setStatus(PurchaseOrder.Status.COMPLETED);
            purchaseOrderRepository.save(order);
        }

        // Create payment record for the monthly order
        Payment payment = Payment.builder()
                .status(1)
                .payType("qr")
                .payName("momo")
                .purchaseOrder(monthlyOrder)
                .build();

        monthlyOrder.setStatus(PurchaseOrder.Status.COMPLETED);
        inOrderRepository.deleteById(orderId);
        paymentRepository.save(payment);
        purchaseOrderRepository.save(monthlyOrder);
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> checkOverdueInvoices(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        if (!user.getRole().getRoleId().equals("113")) {
            throw new AppException(NotExistedErrorCode.USER_EXISTED);
        }

        LocalDateTime overdueThreshold = LocalDateTime.now().minusDays(30);
        List<PurchaseOrder> overdueOrders = purchaseOrderRepository.findByUser_UserIdAndNoteContainingAndStatus(
                userId, "Monthly invoice", PurchaseOrder.Status.COMPLETED);

        List<String> notifications = new ArrayList<>();
        for (PurchaseOrder order : overdueOrders) {
            if (order.getCreatedAt().isBefore(overdueThreshold)) {
                String message = String.format(
                        "Hóa đơn từ %s đến %s (ID: %s) đã quá hạn thanh toán. Vui lòng thanh toán sớm.",
                        order.getNote().split(" ")[2],
                        order.getNote().split(" ")[4],
                        order.getPurchaseOrderId()
                );
                notifications.add(message);
            }
        }

        return notifications;
    }

    private String generateHmacSignature(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }
}
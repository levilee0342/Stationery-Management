package com.project.stationery_be_server.Scheduled;

import com.project.stationery_be_server.entity.InOrder;
import com.project.stationery_be_server.entity.Promotion;
import com.project.stationery_be_server.entity.PurchaseOrder;
import com.project.stationery_be_server.entity.PurchaseOrderDetail;
import com.project.stationery_be_server.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderCleanupService {
    InOrderRepository inOrderRepository;
    PurchaseOrderRepository purchaseOrderRepository;
    PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    ProductDetailRepository productDetailRepository;
    PromotionRepository promotionRepository;

    @Scheduled(fixedRate = 60*1000) // mỗi 60,000ms = 1 phút
    @Transactional
    public void checkExpiredOrders() {
        List<InOrder> expiredOrders = inOrderRepository.findByExpiredTimeBefore(LocalDateTime.now());
        for (InOrder inOrder : expiredOrders){
            String orderId = inOrder.getOrderId();
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Purchase order not found for ID: " + orderId));
            if(purchaseOrder.getUserPromotion() != null){
                Promotion currentPromotion = purchaseOrder.getUserPromotion().getPromotion();
                promotionRepository.increaseUsageCountByPromotionId(currentPromotion.getPromotionId());
            }
            List<PurchaseOrderDetail> details = purchaseOrderDetailRepository.findByPurchaseOrder_PurchaseOrderId(orderId);
            for (PurchaseOrderDetail detail : details) {
                String productDetailId = detail.getProductDetail().getProductDetailId();
                productDetailRepository.restoreQuantity(productDetailId, detail.getQuantity());
                if(detail.getProductPromotion()!= null){
                    promotionRepository.increaseUsageCountByPromotionId(detail.getProductPromotion().getPromotion().getPromotionId());
                }
            }
            purchaseOrderDetailRepository.deleteAll(details);
            purchaseOrderRepository.deleteById(orderId);
            inOrderRepository.deleteById(orderId);
        }
    }
}

package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String> {
    Optional<PurchaseOrder> findByPurchaseOrderId(String purchaseOrderId);

    @Query("SELECT po FROM PurchaseOrder po LEFT JOIN FETCH po.purchaseOrderDetails WHERE po.user.userId = :userId AND po.status = :status")
    List<PurchaseOrder> findByUser_UserIdAndStatusWithDetails(@Param("userId") String userId, @Param("status") PurchaseOrder.Status status);

    @Query("SELECT o FROM PurchaseOrder o LEFT JOIN FETCH o.purchaseOrderDetails WHERE o.user.userId = :userId")
    List<PurchaseOrder> findByUser_UserIdWithDetails(@Param("userId") String userId);

    List<PurchaseOrder> findByUser_UserIdAndCreatedAtBetween(String userId, LocalDateTime start, LocalDateTime end);

    List<PurchaseOrder> findByUser_UserIdAndNoteContainingAndStatus(String userId, String note, PurchaseOrder.Status status);

    @Query("SELECT po FROM PurchaseOrder po WHERE po.user.userId = :userId AND po.note LIKE %:note% ORDER BY po.createdAt DESC")
    PurchaseOrder findTopByUser_UserIdAndNoteContainingOrderByCreatedAtDesc(@Param("userId") String userId, @Param("note") String note);

    @Query("SELECT po FROM PurchaseOrder po JOIN FETCH po.purchaseOrderDetails " +
            "JOIN po.user u JOIN u.role r " +
            "WHERE po.status = :status " +
            "AND (:roleName IS NULL OR r.roleName = :roleName)")
    Page<PurchaseOrder> findPendingOrdersByFilters(
            @Param("roleName") String roleName,
            @Param("status") PurchaseOrder.Status status,
            Pageable pageable);

    @Query("SELECT po FROM PurchaseOrder po " +
            "JOIN FETCH po.purchaseOrderDetails " +
            "JOIN po.user u JOIN u.role r " +
            "WHERE po.status IN :statuses " +
            "AND (:roleName IS NULL OR r.roleName = :roleName)")
    Page<PurchaseOrder> findNonPendingOrdersByFilters(
            @Param("roleName") String roleName,
            @Param("statuses") List<PurchaseOrder.Status> statuses,
            Pageable pageable);

    List<PurchaseOrder> findByUser_UserIdAndCreatedAtLessThanEqualAndStatusIn(
            String userId,
            LocalDateTime endDate,
            List<PurchaseOrder.Status> statuses
    );

    List<PurchaseOrder> findByUser_UserId(String userId);

    List<PurchaseOrder> findByUser_UserIdAndStatus(String userId, PurchaseOrder.Status status);
}
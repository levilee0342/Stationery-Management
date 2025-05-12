package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByEmail (String username);
    boolean existsByEmail(String email);
    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.user.userId = :userId")
    boolean hasReview(@Param("userId") String userId);

    @Query("SELECT COUNT(a) > 0 FROM Address a WHERE a.user.userId = :userId")
    boolean hasAddress(@Param("userId") String userId);

    @Query("SELECT COUNT(c) > 0 FROM Cart c WHERE c.user.userId = :userId")
    boolean hasCart(@Param("userId") String userId);

    @Query("SELECT COUNT(po) > 0 FROM PurchaseOrder po WHERE po.user.userId = :userId")
    boolean hasPurchaseOrder(@Param("userId") String userId);

    @Query("SELECT COUNT(up) > 0 FROM UserPromotion up WHERE up.user.userId = :userId")
    boolean hasUserPromotion(@Param("userId") String userId);
}
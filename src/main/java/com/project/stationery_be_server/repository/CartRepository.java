package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.Cart;
import com.project.stationery_be_server.entity.CartId;
import com.project.stationery_be_server.entity.ProductDetail;
import com.project.stationery_be_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, CartId> {

    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId")
    List<Cart> findByUserId(@Param("userId") String userId);

    @Query("SELECT c FROM Cart c WHERE c.cartId = :cartId")
    Optional<Cart> findByCartId(@Param("cartId") CartId cartId);

    @Query("SELECT c FROM Cart c WHERE c.productDetail.product.productId = :productId AND c.user.userId = :userId")
    List<Cart> findByUserIdAndProductId(@Param("userId") String userId, @Param("productId") String productId);

    List<Cart> findByUser_UserId(String userId);
    void deleteByUser_UserId(String userId);

    void deleteByUser_UserIdAndProductDetail_ProductDetailId(String userId, String productDetail);

    List<Cart> user(User user);
    long countByProductDetail_ProductDetailId(String productDetailId);
}

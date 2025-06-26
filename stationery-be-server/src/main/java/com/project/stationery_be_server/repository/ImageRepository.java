package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ImageRepository  extends JpaRepository<Image, String> {
    List<Image> findByProduct_ProductIdAndColor_ColorIdOrderByPriorityAsc(String productId,String colorId);
    Image findFirstByProduct_ProductIdAndColor_ColorIdOrderByPriorityAsc(String productId, String colorId);

    List<Image> findByProduct_ProductIdOrderByPriorityAsc(String productId);
    Image findFirstByProduct_ProductIdAndColorIsNullOrderByPriorityAsc(String productId);

    long countByProduct_ProductId(String productId);

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO image (,url, priority, product_id, color_id) " +
                    "VALUES ( :url, :priority, :productId, :colorId)",
            nativeQuery = true
    )
    void insertImage(
            @Param("url") String url,
            @Param("priority") int priority,
            @Param("productId") String productId,
            @Param("colorId") String colorId
    );
}

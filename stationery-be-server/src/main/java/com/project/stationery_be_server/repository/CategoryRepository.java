package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.Category;
import com.project.stationery_be_server.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, String>, JpaSpecificationExecutor<Category> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.categoryName = :categoryName")
    boolean existsByCategoryName(@Param("categoryName") String categoryName);

    <Optional> Category findByCategoryId(String categoryId);
}
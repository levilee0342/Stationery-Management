package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.Category;
import com.project.stationery_be_server.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ColorRepository extends JpaRepository<Color,String>, JpaSpecificationExecutor<Color> {
}

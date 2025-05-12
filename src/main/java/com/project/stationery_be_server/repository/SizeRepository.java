package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<Size,String> {
}

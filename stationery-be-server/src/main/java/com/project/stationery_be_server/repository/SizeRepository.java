package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.Size;
import com.project.stationery_be_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SizeRepository extends JpaRepository<Size,String> , JpaSpecificationExecutor<Size> {
}

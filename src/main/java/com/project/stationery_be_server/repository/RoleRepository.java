package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface RoleRepository extends JpaRepository<Role, String> {
}
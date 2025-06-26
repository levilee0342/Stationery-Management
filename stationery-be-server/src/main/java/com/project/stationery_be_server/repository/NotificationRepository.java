package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(String userId);
    long countByUser_UserIdAndIsReadFalse(String userId);
}

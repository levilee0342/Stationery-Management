package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.Image;
import com.project.stationery_be_server.entity.InOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InOrderRepository  extends JpaRepository<InOrder, String> {

    List<InOrder> findByExpiredTimeBefore(LocalDateTime time);

}

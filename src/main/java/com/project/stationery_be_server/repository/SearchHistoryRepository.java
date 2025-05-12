package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, String> {
    @Query("SELECT sh.keyword, COUNT(sh.keyword) as searchCount " +
            "FROM SearchHistory sh " +
            "GROUP BY sh.keyword " +
            "ORDER BY searchCount DESC")
    List<Object[]> findTop10Keywords();

    @Query("SELECT sh FROM SearchHistory sh WHERE sh.user.userId = :userId ORDER BY sh.createdAt ASC")
    List<SearchHistory> findByUserIdOrderByCreatedAtAsc(String userId);

    List<SearchHistory> findByUserIsNull();
}
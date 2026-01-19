package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.Enums.SearchLogType;
import com.example.otoportdeneme.models.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    List<SearchLog> findByTypeOrderByCreatedAtDesc(SearchLogType type);

    List<SearchLog> findByClientIdOrderByCreatedAtDesc(Long clientId);

    List<SearchLog> findByTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
            SearchLogType type, Instant from, Instant to
    );
}


package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.ExpertItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpertItemRepository extends JpaRepository<ExpertItem, Long> {
    List<ExpertItem> findByReportId(Long reportId);
}

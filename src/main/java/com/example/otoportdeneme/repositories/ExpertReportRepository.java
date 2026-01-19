package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.ExpertReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpertReportRepository extends JpaRepository<ExpertReport, Long> {
    Optional<ExpertReport> findByCarId(Long carId);
}

package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    List<MaintenanceRecord> findByCarIdOrderByServiceDateDesc(Long carId);
}


package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.ExpertReport;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExpertReportRepository extends JpaRepository<ExpertReport, Long> {
    Optional<ExpertReport> findByCar_Id(Long carId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select er from ExpertReport er
        left join fetch er.items
        where er.car.id = :carId
    """)
    Optional<ExpertReport> findByCarIdForUpdate(@Param("carId") Long carId);

    @Modifying
    @Query("delete from ExpertItem i where i.report.id = :reportId")
    void deleteByReportId(@Param("reportId") Long reportId);

    //2phase locking
}

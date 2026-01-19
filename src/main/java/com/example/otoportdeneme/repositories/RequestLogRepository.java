package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.RequestLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {

    long countByCreatedAtBetween(Instant start, Instant end);

    long countByIsGuestAndCreatedAtBetween(Boolean isGuest, Instant start, Instant end);

    // Hourly count (MySQL) - Eğer PostgreSQL kullanıyorsan aşağıdaki query’i değiştirmen gerekir.
    @Query(value = """
        SELECT HOUR(FROM_UNIXTIME(UNIX_TIMESTAMP(created_at))) as h, COUNT(*) as c
        FROM request_logs
        WHERE created_at >= :start AND created_at < :end
        GROUP BY HOUR(FROM_UNIXTIME(UNIX_TIMESTAMP(created_at)))
        ORDER BY h
    """, nativeQuery = true)
    List<Object[]> hourlyCounts(@Param("start") Instant start, @Param("end") Instant end);

    @Query(value = """
        SELECT DATE(FROM_UNIXTIME(UNIX_TIMESTAMP(created_at))) as d, COUNT(*) as c
        FROM request_logs
        WHERE created_at >= :start AND created_at < :end
        GROUP BY DATE(FROM_UNIXTIME(UNIX_TIMESTAMP(created_at)))
        ORDER BY d
    """, nativeQuery = true)
    List<Object[]> dailyCounts(@Param("start") Instant start, @Param("end") Instant end);

    @Query(value = """
        SELECT DATE(FROM_UNIXTIME(UNIX_TIMESTAMP(created_at))) as d, COUNT(*) as c
        FROM request_logs
        WHERE is_guest = 1 AND created_at >= :start AND created_at < :end
        GROUP BY DATE(FROM_UNIXTIME(UNIX_TIMESTAMP(created_at)))
        ORDER BY d
    """, nativeQuery = true)
    List<Object[]> dailyGuestCounts(@Param("start") Instant start, @Param("end") Instant end);

    @Query(value = """
        SELECT DATE(FROM_UNIXTIME(UNIX_TIMESTAMP(created_at))) as d, COUNT(*) as c
        FROM request_logs
        WHERE is_guest = 0 AND created_at >= :start AND created_at < :end
        GROUP BY DATE(FROM_UNIXTIME(UNIX_TIMESTAMP(created_at)))
        ORDER BY d
    """, nativeQuery = true)
    List<Object[]> dailyUserCounts(@Param("start") Instant start, @Param("end") Instant end);
}

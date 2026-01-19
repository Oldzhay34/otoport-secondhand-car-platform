package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.GuestAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface GuestAccessLogRepository extends JpaRepository<GuestAccessLog, Long> {

    long countByCreatedAtBetween(Instant start, Instant end);

    @Query("""
        select function('hour', g.createdAt), count(g)
        from GuestAccessLog g
        where g.createdAt >= :start and g.createdAt < :end
        group by function('hour', g.createdAt)
        order by function('hour', g.createdAt)
    """)
    List<Object[]> countHourly(@org.springframework.data.repository.query.Param("start") Instant start,
                               @org.springframework.data.repository.query.Param("end") Instant end);

    @Query("""
        select count(g)
        from GuestAccessLog g
        where g.createdAt >= :start and g.createdAt < :end
    """)
    long countBetween(@org.springframework.data.repository.query.Param("start") Instant start,
                      @org.springframework.data.repository.query.Param("end") Instant end);
}


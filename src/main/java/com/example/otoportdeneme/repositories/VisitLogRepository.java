package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    long countByCreatedAtBetween(Instant start, Instant end);

    @Query("""
      select v.actorType, count(v)
      from VisitLog v
      where v.createdAt >= :start and v.createdAt < :end
      group by v.actorType
    """)
    List<Object[]> countByActorType(Instant start, Instant end);

    @Query("""
      select function('hour', v.createdAt), count(v)
      from VisitLog v
      where v.createdAt >= :start and v.createdAt < :end
      group by function('hour', v.createdAt)
      order by function('hour', v.createdAt)
    """)
    List<Object[]> countHourly(Instant start, Instant end);
}

package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.Enums.ActorType;
import com.example.otoportdeneme.Enums.AuditAction;
import com.example.otoportdeneme.models.AuditLog;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByActorTypeAndActorIdOrderByCreatedAtDesc(ActorType actorType, Long actorId);

    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant from, Instant to);

    // ✅ admin dashboard feed için (LIMIT destekli)
    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant start, Instant end, Pageable pageable);

    // ✅ listing bazlı action count (sende vardı)
    @Query("""
       select a.entityId, count(a)
       from AuditLog a
       where a.entityType = 'LISTING'
         and a.action = :action
         and a.createdAt >= :start and a.createdAt < :end
       group by a.entityId
    """)
    List<Object[]> countListingActions(AuditAction action, Instant start, Instant end);

    // ✅ store bazlı listing action count (admin analytics)
    @Query("""
      select a.actorId, count(a)
      from AuditLog a
      where a.actorType = com.example.otoportdeneme.Enums.ActorType.STORE
        and a.entityType = 'LISTING'
        and a.action = :action
        and a.createdAt >= :start and a.createdAt < :end
      group by a.actorId
    """)
    List<Object[]> countStoreListingActions(AuditAction action, Instant start, Instant end);

    // ✅ daily login breakdown (AdminAnalyticsServiceImpl kullanıyor)
    @Query("""
      select a.actorType, count(a)
      from AuditLog a
      where a.action = :action
        and a.createdAt >= :start and a.createdAt < :end
      group by a.actorType
    """)
    List<Object[]> countByActorTypeForAction(AuditAction action, Instant start, Instant end);

    // ✅ hourly audit yoğunluğu (AdminAnalyticsServiceImpl kullanıyor)
    @Query("""
      select function('hour', a.createdAt), count(a)
      from AuditLog a
      where a.createdAt >= :start and a.createdAt < :end
      group by function('hour', a.createdAt)
      order by function('hour', a.createdAt)
    """)
    List<Object[]> countHourlyAll(Instant start, Instant end);

    @Query("""
  select a from AuditLog a
  where a.createdAt >= :start and a.createdAt < :end
  order by a.createdAt desc
""")
    List<AuditLog> findRecentBetween(Instant start, Instant end, Pageable pageable);


}

package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.MessageModerationAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface MessageModerationAttemptRepository extends JpaRepository<MessageModerationAttempt, Long> {

    @Query("""
      select a.actorType, a.actorId, count(a)
      from MessageModerationAttempt a
      where a.createdAt >= :since
      group by a.actorType, a.actorId
      order by count(a) desc
    """)
    List<Object[]> topActorsSince(Instant since);
}

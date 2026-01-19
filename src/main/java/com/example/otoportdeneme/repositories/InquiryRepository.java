package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByStoreIdOrderByCreatedAtDesc(Long storeId);
    List<Inquiry> findByClientIdOrderByCreatedAtDesc(Long clientId);
    List<Inquiry> findByListingIdOrderByCreatedAtDesc(Long listingId);
    @Query("""
      select i.store.id, count(i)
      from Inquiry i
      where i.createdAt >= :start and i.createdAt < :end
      group by i.store.id
    """)
    List<Object[]> countByStore(Instant start, Instant end);
    @Query("""
      select i.store.id, count(i)
      from Inquiry i
      where i.createdAt >= :start and i.createdAt < :end
      group by i.store.id
    """)
    List<Object[]> countByStoreBetween(Instant start, Instant end);
}

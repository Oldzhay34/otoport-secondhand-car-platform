package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByStoreIdOrderByCreatedAtDesc(Long storeId);


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

    Optional<Inquiry> findByListingIdAndGuestEmailIgnoreCase(Long listingId, String guestEmail);

    @Query("""
        select i
        from Inquiry i
        join fetch i.listing l
        left join fetch i.client c
        where i.store.id = :storeId
        order by i.createdAt desc
    """)
    List<Inquiry> findByStoreIdFetchListingClient(@Param("storeId") Long storeId);

    @Query("""
        select i
        from Inquiry i
        join fetch i.listing l
        left join fetch i.client c
        where i.id = :inquiryId
    """)
    Optional<Inquiry> findByIdFetchListingClient(@Param("inquiryId") Long inquiryId);

    // client service tarafında lazysiz map için
    @Query("""
        select i
        from Inquiry i
        join fetch i.listing l
        join fetch i.store s
        left join fetch i.client c
        where i.id = :id
    """)
    Optional<Inquiry> findByIdFetchAll(@Param("id") Long id);

    @Query("""
        select i
        from Inquiry i
        join fetch i.listing l
        join fetch i.store s
        left join fetch i.client c
        where l.id = :listingId and c.id = :clientId
    """)
    Optional<Inquiry> findByListingIdAndClientIdFetchAll(@Param("listingId") Long listingId,
                                                         @Param("clientId") Long clientId);

    // sende vardı:
    Optional<Inquiry> findByListingIdAndClientId(Long listingId, Long clientId);

}

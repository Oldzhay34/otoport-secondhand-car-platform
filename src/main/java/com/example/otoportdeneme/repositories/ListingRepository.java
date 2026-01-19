package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.Enums.ListingStatus;
import com.example.otoportdeneme.models.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {

    long countByStoreIdAndStatus(Long storeId, ListingStatus status);

    @Modifying
    @Query("update Listing l set l.favoriteCount = l.favoriteCount + 1 where l.id = :id")
    int incrementFavoriteCount(@Param("id") Long id);

    @Modifying
    @Query("""
           update Listing l
              set l.favoriteCount = case when l.favoriteCount > 0
                                         then l.favoriteCount - 1
                                         else 0 end
            where l.id = :id
           """)
    int decrementFavoriteCount(@Param("id") Long id);

    @Query("""
        select distinct l
        from Listing l
        join fetch l.store s
        join fetch l.car c
        join fetch c.trim t
        join fetch t.model m
        join fetch m.brand b
        left join fetch l.images imgs
        """)
    List<Listing> findAllForCard();
    @Query("""
        select distinct l
        from Listing l
        join fetch l.car c
        left join fetch c.trim t
        left join fetch t.model m
        left join fetch m.brand b
        where l.store.id = :storeId
          and l.status = com.example.otoportdeneme.Enums.ListingStatus.ACTIVE
    """)
    List<Listing> findActiveByStoreIdFetchCarTree(@Param("storeId") Long storeId);
    @Query("""
        select distinct l
        from Listing l
        left join fetch l.store s
        left join fetch l.car c
        left join fetch c.trim t
        left join fetch t.model m
        left join fetch m.brand b
        where l.id = :listingId
    """)
    Listing findByIdFetchAll(@Param("listingId") Long listingId);

    @Query("""
    select distinct l
    from Listing l
    left join fetch l.store s
    left join fetch l.car c
    left join fetch c.trim t
    left join fetch t.model m
    left join fetch m.brand b
    left join fetch l.images i
    where s.id = :storeId
      and l.status = :status
""")
    List<Listing> findByStoreIdAndStatusFetchAll(
            @Param("storeId") Long storeId,
            @Param("status") ListingStatus status
    );
    @Query("""
        select distinct l from Listing l
        left join fetch l.images imgs
        join fetch l.store s
        join fetch l.car c
        join fetch c.trim t
        join fetch t.model m
        join fetch m.brand b
        where l.id = :id
    """)
    Listing findDetailById(@Param("id") Long id);

    // ✅ filtre için: store + car + trim/model/brand + images (cover için)
    @Query("""
        select distinct l from Listing l
        left join fetch l.images imgs
        join fetch l.store s
        join fetch l.car c
        join fetch c.trim t
        join fetch t.model m
        join fetch m.brand b
        where (:storeId is null or s.id = :storeId)
        and (:status is null or l.status = :status)
    """)
    List<Listing> findForFilter(@Param("storeId") Long storeId,
                                @Param("status") ListingStatus status);

    List<Listing> findByStoreIdOrderByCreatedAtDesc(Long storeId);

    List<Listing> findByStoreIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(Long storeId, String q);
    @Query("""
        select distinct l
        from Listing l
        left join fetch l.images imgs
        where l.store.id = :storeId
        order by l.createdAt desc
    """)
    List<Listing> findByStoreIdWithImagesOrderByCreatedAtDesc(@Param("storeId") Long storeId);

    @Query("""
        select distinct l
        from Listing l
        left join fetch l.images imgs
        where l.store.id = :storeId
          and lower(l.title) like lower(concat('%', :q, '%'))
        order by l.createdAt desc
    """)
    List<Listing> findByStoreIdAndTitleContainingIgnoreCaseWithImagesOrderByCreatedAtDesc(
            @Param("storeId") Long storeId,
            @Param("q") String q
    );
    @Modifying
    @Query("update Listing l set l.viewCount = l.viewCount + 1 where l.id = :id")
    void incrementViewCount(@Param("id") Long id);
}

// concurrent sağlam lost update ve temporary update problemleri engelleri çıkmaz artık
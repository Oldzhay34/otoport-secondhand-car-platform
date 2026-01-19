package com.example.otoportdeneme.repositories;


import com.example.otoportdeneme.dto_Objects.FavoriteCardDto;
import com.example.otoportdeneme.models.Favorite;
import com.example.otoportdeneme.models.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    // listeleme için tutulan favoriler
    List<Favorite> findByClientIdOrderByCreatedAtDesc(Long clientId);

    // burada favori var mı diye kontrol ediyorum
    boolean existsByClientIdAndListingId(Long clientId, Long listingId);

    // favorileri bununla siliyoruz
    void deleteByClientIdAndListingId(Long clientId, Long listingId);
    // ✅ LAZY sorunu yok: DTO direkt query'den geliyor
    @Query("""
    select new com.example.otoportdeneme.dto_Objects.FavoriteCardDto(
        l.id,
        l.title,
        l.price,
        l.currency,
        l.city,
        c.year,
        c.kilometer,
        (
            select li.imagePath
            from ListingImage li
            where li.listing = l
              and li.sortOrder = (
                select min(li2.sortOrder)
                from ListingImage li2
                where li2.listing = l
              )
        ),
        f.createdAt
    )
    from Favorite f
    join f.listing l
    join l.car c
    where f.client.id = :clientId
    order by f.createdAt desc
""")
    List<FavoriteCardDto> findCardsByClientId(@Param("clientId") Long clientId);


}

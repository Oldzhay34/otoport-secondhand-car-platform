package com.example.otoportdeneme.repositories;


import com.example.otoportdeneme.Enums.PlacementType;
import com.example.otoportdeneme.models.FeaturedPlacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface FeaturedPlacementRepository extends JpaRepository<FeaturedPlacement, Long> {

    // ✅ Ana sayfa üst reklam alanı: aktif + tarih aralığında + priority yüksekten
    @Query("""
            select fp from FeaturedPlacement fp
            where fp.type = :type
              and fp.isActive = true
              and fp.startAt <= :now
              and fp.endAt >= :now
            order by fp.priority desc, fp.sortOrder asc, fp.id desc
           """)
    List<FeaturedPlacement> findActivePlacements(PlacementType type, Instant now);
}

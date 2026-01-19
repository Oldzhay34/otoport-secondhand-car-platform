package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.ListingView;
import com.example.otoportdeneme.models.ListingViewId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingViewRepository extends JpaRepository<ListingView, ListingViewId> {
    boolean existsByIdUserIdAndIdListingId(Long userId, Long listingId);
}
